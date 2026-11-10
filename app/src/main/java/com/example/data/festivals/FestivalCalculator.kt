package com.example.data.festivals

import com.example.data.LunarCalendar
import com.example.data.PanchangNames
import com.example.data.model.FestivalCalendarItem
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.format.TextStyle
import java.util.Locale

/** Turns festival rules into dates for one year at one location. */
class FestivalCalculator(private val data: FestivalData) {

    private class Found(
        val text: FestivalText,
        val date: LocalDate,
        val major: Boolean,
        val tithiEn: String,
        val tithiTe: String,
        val absoluteTithi: Int? = null,
        val noteEn: String = "",
        val noteTe: String = "",
        val verified: Boolean = false
    )

    fun calculate(year: Int, lat: Double, lon: Double): List<FestivalCalendarItem> {
        val from = LocalDate.of(year, 1, 1).minusDays(60).atStartOfDay()
        val to = LocalDate.of(year, 12, 31).plusDays(60).atStartOfDay()
        val months = LunarCalendar.monthsBetween(from, to)
        val place = Place(lat, lon)

        val found = mutableListOf<Found>()
        val namedTithis = mutableSetOf<Pair<Int, Int>>()
        val explicitSankrantis = data.rules.filter { it.type == "sankranti" }.mapNotNull { it.sign }.toSet()

        for (rule in data.rules) {
            when (rule.type) {
                "tithi" -> months.filter { it.index == rule.month && !it.isAdhika }.forEach { month ->
                    namedTithis += month.index to rule.absoluteTithi()
                    val day = place.dayOf(month, rule.absoluteTithi(), rule.kala)
                    found += withOffsets(rule, day, tithiLabel(month, rule.absoluteTithi()), rule.absoluteTithi(), place)
                }

                "weekdayBeforeTithi" -> months.filter { it.index == rule.month && !it.isAdhika }.forEach { month ->
                    val anchor = place.dayOf(month, rule.absoluteTithi(), Kala.SUNRISE)
                    val pakshaStart = LunarCalendar.tithiInterval(month, rule.absoluteTithi() - rule.tithi + 1).first.toLocalDate()
                    val day = generateSequence(anchor.minusDays(1)) { it.minusDays(1) }
                        .takeWhile { it >= pakshaStart }
                        .firstOrNull { it.dayOfWeek == rule.weekday }
                    if (day != null) found += withOffsets(rule, day, place.sunriseLabel(day), null, place)
                }

                "tithiWithSunSign" -> months.filter { !it.isAdhika }.forEach { month ->
                    val day = place.dayOf(month, rule.absoluteTithi(), rule.kala)
                    if (LunarCalendar.sunSign(place.sunrise(day)) == rule.sunSign) {
                        found += withOffsets(rule, day, tithiLabel(month, rule.absoluteTithi()), rule.absoluteTithi(), place)
                    }
                }

                "everyTithi" -> months.forEach { month ->
                    val pakshas = if (rule.paksha == Paksha.BOTH) listOf(Paksha.SHUKLA, Paksha.KRISHNA) else listOf(rule.paksha)
                    pakshas.forEach { paksha ->
                        val tithi = rule.absoluteTithi(paksha)
                        if (month.isAdhika || (month.index to tithi) !in namedTithis) {
                            val day = place.dayOf(month, tithi, rule.kala)
                            found += Found(
                                text = fill(rule.text, month, tithi),
                                date = day,
                                major = rule.major,
                                tithiEn = tithiLabel(month, tithi).first,
                                tithiTe = tithiLabel(month, tithi).second,
                                absoluteTithi = tithi
                            )
                        }
                    }
                }

                "sankranti", "everySankranti" -> LunarCalendar.sankrantis(from, to)
                    .filter { (_, sign) -> if (rule.type == "sankranti") sign == rule.sign else sign !in explicitSankrantis }
                    .forEach { (enters, sign) ->
                        // Entry after sunset counts for the next day.
                        val day = enters.toLocalDate().let { if (enters >= place.sunset(it)) it.plusDays(1) else it }
                        val label = "Sun enters ${LunarCalendar.SIGN_NAMES_EN[sign]}" to
                            "సూర్యుడు ${LunarCalendar.SIGN_NAMES_TE[sign]} రాశిలోకి ప్రవేశం"
                        val text = rule.text.copy(
                            nameEn = rule.text.nameEn.replace("{sign}", LunarCalendar.SIGN_NAMES_EN[sign]),
                            nameTe = rule.text.nameTe.replace("{sign}", LunarCalendar.SIGN_NAMES_TE[sign])
                        )
                        found += withOffsets(rule.copy(text = text), day, label, null, place)
                    }

                else -> error("Unknown festival rule type \"${rule.type}\" in ${rule.text.id}")
            }
        }

        // A recurring vrat on the same day and tithi as a named festival adds nothing.
        val majorDays = found.filter { it.major }.map { it.date to it.absoluteTithi }.toSet()
        found.removeAll { !it.major && it.absoluteTithi != null && (it.date to it.absoluteTithi) in majorDays }

        data.fixed.forEach { fixed ->
            val dates = fixed.monthDay?.let { listOf(MonthDay.parse("--$it").atYear(year)) } ?: fixed.dates
            dates.forEach { date ->
                found += Found(
                    text = fixed.text,
                    date = date,
                    major = fixed.major,
                    tithiEn = "",
                    tithiTe = "",
                    noteEn = if (fixed.moonSighting) "Subject to moon sighting" else "",
                    noteTe = if (fixed.moonSighting) "చంద్ర దర్శనాన్ని బట్టి మారవచ్చు" else ""
                )
            }
        }

        applyOverrides(found, year, place)

        return found
            .filter { it.date.year == year }
            .sortedWith(compareBy<Found> { it.date }.thenBy { !it.major })
            .map { it.toItem() }
    }

    // Overrides replace a calculated date (or add one the rules missed) and mark it verified.
    private fun applyOverrides(found: MutableList<Found>, year: Int, place: Place) {
        data.overrides.filter { it.year == year }.forEach { override ->
            val existing = found.filter { it.text.id == override.id && it.date.year == year }
            val template = existing.firstOrNull()
                ?: knownTexts()[override.id]?.let { (text, major) ->
                    Found(text, override.date, major, "", "")
                }
                ?: return@forEach
            found.removeAll(existing)
            val (tithiEn, tithiTe) = if (template.tithiEn.isNotEmpty()) {
                template.tithiEn to template.tithiTe
            } else {
                place.sunriseLabel(override.date)
            }
            found += Found(
                text = template.text,
                date = override.date,
                major = template.major,
                tithiEn = tithiEn,
                tithiTe = tithiTe,
                absoluteTithi = template.absoluteTithi,
                noteEn = override.source,
                noteTe = override.source,
                verified = true
            )
        }
    }

    private fun knownTexts(): Map<String, Pair<FestivalText, Boolean>> =
        data.rules.flatMap { rule -> listOf(rule.text to rule.major) + rule.offsets.map { it.text to rule.major } }
            .plus(data.fixed.map { it.text to it.major })
            .associateBy({ it.first.id }, { it })

    private fun withOffsets(
        rule: FestivalRule,
        day: LocalDate,
        label: Pair<String, String>,
        absoluteTithi: Int?,
        place: Place
    ): List<Found> =
        listOf(Found(rule.text, day, rule.major, label.first, label.second, absoluteTithi)) +
            rule.offsets.map { offset ->
                val offsetDay = day.plusDays(offset.days)
                val offsetLabel = place.sunriseLabel(offsetDay)
                Found(offset.text, offsetDay, rule.major, offsetLabel.first, offsetLabel.second)
            }

    private fun fill(text: FestivalText, month: LunarCalendar.LunarMonth, tithi: Int): FestivalText {
        val name = PanchangNames.getTithi(tithi)
        return text.copy(
            nameEn = text.nameEn.replace("{month}", month.nameEn).replace("{tithi}", name.nameEn),
            nameTe = text.nameTe.replace("{month}", month.nameTe).replace("{tithi}", name.nameTe)
        )
    }

    private fun tithiLabel(month: LunarCalendar.LunarMonth, tithi: Int): Pair<String, String> {
        val name = PanchangNames.getTithi(tithi)
        return "${month.nameEn} ${name.nameEn}" to "${month.nameTe} ${name.nameTe}"
    }

    private fun Found.toItem() = FestivalCalendarItem(
        id = "${text.id}_$date",
        baseId = text.id,
        nameEn = text.nameEn,
        nameTe = text.nameTe,
        dateString = date.toString(),
        monthGroupEn = "${date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${date.year}",
        monthGroupTe = "${TELUGU_MONTHS[date.monthValue - 1]} ${date.year}",
        tithiEn = tithiEn,
        tithiTe = tithiTe,
        descriptionEn = text.descriptionEn,
        descriptionTe = text.descriptionTe,
        ritualsEn = text.ritualsEn,
        ritualsTe = text.ritualsTe,
        isMajor = major,
        isVerified = verified,
        noteEn = noteEn,
        noteTe = noteTe
    )

    /** Day-selection rules for one location. */
    private class Place(val lat: Double, val lon: Double) {
        fun sunrise(day: LocalDate) = LunarCalendar.sunriseAt(day, lat, lon)
        fun sunset(day: LocalDate) = LunarCalendar.sunsetAt(day, lat, lon)

        fun sunriseLabel(day: LocalDate): Pair<String, String> {
            val lunar = LunarCalendar.getLunarMonth(day, lat, lon)
            val name = PanchangNames.getTithi(lunar.tithi)
            return "${lunar.month.nameEn} ${name.nameEn}" to "${lunar.month.nameTe} ${name.nameTe}"
        }

        /**
         * The first day whose [kala] contains the tithi; if no day does (a skipped tithi),
         * the day the tithi covers longest.
         */
        fun dayOf(month: LunarCalendar.LunarMonth, tithi: Int, kala: Kala): LocalDate {
            val (start, end) = LunarCalendar.tithiInterval(month, tithi)
            val days = generateSequence(start.toLocalDate().minusDays(1)) { it.plusDays(1) }
                .takeWhile { it <= end.toLocalDate() }
                .toList()
            return days.firstOrNull { day -> contains(window(day, kala), start, end) }
                ?: days.maxBy { day -> overlap(sunrise(day), sunrise(day.plusDays(1)), start, end) }
        }

        private fun window(day: LocalDate, kala: Kala): Pair<LocalDateTime, LocalDateTime> {
            val rise = sunrise(day)
            val set = sunset(day)
            val dayLength = Duration.between(rise, set)
            fun dayPart(fifths: Long) = rise.plus(dayLength.multipliedBy(fifths).dividedBy(5))
            return when (kala) {
                Kala.SUNRISE -> rise to rise
                Kala.MADHYAHNA -> dayPart(2) to dayPart(3)
                Kala.APARAHNA -> dayPart(3) to dayPart(4)
                Kala.PRADOSHA -> set to set.plusMinutes(PRADOSHA_MINUTES)
                Kala.NISHITA -> {
                    // The 8th of the 15 muhurtas between sunset and the next sunrise.
                    val night = Duration.between(set, sunrise(day.plusDays(1)))
                    set.plus(night.multipliedBy(7).dividedBy(15)) to set.plus(night.multipliedBy(8).dividedBy(15))
                }
            }
        }

        private fun contains(
            window: Pair<LocalDateTime, LocalDateTime>,
            start: LocalDateTime,
            end: LocalDateTime
        ): Boolean {
            val (from, to) = window
            return if (from == to) from >= start && from < end else overlap(from, to, start, end) > 0
        }

        private fun overlap(from: LocalDateTime, to: LocalDateTime, start: LocalDateTime, end: LocalDateTime): Long {
            val latestStart = maxOf(from, start)
            val earliestEnd = minOf(to, end)
            return if (earliestEnd > latestStart) Duration.between(latestStart, earliestEnd).seconds else 0
        }
    }

    companion object {
        private const val PRADOSHA_MINUTES = 144L

        private val TELUGU_MONTHS = listOf(
            "జనవరి", "ఫిబ్రవరి", "మార్చి", "ఏప్రిల్", "మే", "జూన్",
            "జూలై", "ఆగస్టు", "సెప్టెంబర్", "అక్టోబర్", "నవంబర్", "డిసెంబర్"
        )
    }
}
