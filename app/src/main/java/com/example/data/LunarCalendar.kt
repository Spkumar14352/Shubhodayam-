package com.example.data

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.floor

/**
 * Telugu amanta lunar calendar built on [AstronomyEngine].
 * Every LocalDateTime here is Asia/Kolkata wall time, the engine's convention.
 */
object LunarCalendar {
    val MONTH_NAMES_EN = listOf(
        "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha", "Shravana", "Bhadrapada",
        "Ashwayuja", "Kartika", "Margashira", "Pushya", "Magha", "Phalguna"
    )
    val MONTH_NAMES_TE = listOf(
        "చైత్ర", "వైశాఖ", "జ్యేష్ఠ", "ఆషాఢ", "శ్రావణ", "భాద్రపద",
        "ఆశ్వయుజ", "కార్తీక", "మార్గశిర", "పుష్య", "మాఘ", "ఫాల్గుణ"
    )
    val SIGN_NAMES_EN = listOf(
        "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
        "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena"
    )
    val SIGN_NAMES_TE = listOf(
        "మేష", "వృషభ", "మిథున", "కర్కాటక", "సింహ", "కన్య",
        "తుల", "వృశ్చిక", "ధనుస్సు", "మకర", "కుంభ", "మీన"
    )

    private const val TITHI_SPAN = 12.0
    private val MOON_STEP: Duration = Duration.ofHours(6)

    /** A lunar month from one new moon to the next. [index] 0 = Chaitra … 11 = Phalguna. */
    data class LunarMonth(
        val index: Int,
        val isAdhika: Boolean,
        val start: LocalDateTime,
        val end: LocalDateTime
    ) {
        val nameEn: String get() = (if (isAdhika) "Adhika " else "") + MONTH_NAMES_EN[index]
        val nameTe: String get() = (if (isAdhika) "అధిక " else "") + MONTH_NAMES_TE[index]
    }

    /** The lunar month and tithi (1–30) in effect at sunrise. */
    data class LunarDay(val month: LunarMonth, val tithi: Int) {
        val isShukla: Boolean get() = tithi <= 15
        val pakshaEn: String get() = if (isShukla) "Shukla Paksha" else "Krishna Paksha"
        val pakshaTe: String get() = if (isShukla) "శుక్ల పక్షం" else "కృష్ణ పక్షం"
    }

    fun getLunarMonth(date: LocalDate, lat: Double, lon: Double): LunarDay {
        val sunrise = sunriseAt(date, lat, lon)
        val start = newMoonOnOrBefore(sunrise)
        return LunarDay(monthFrom(start, nextNewMoon(start)), tithiAt(sunrise))
    }

    /** Every lunar month overlapping [from, to], in order. */
    fun monthsBetween(from: LocalDateTime, to: LocalDateTime): List<LunarMonth> {
        val months = mutableListOf<LunarMonth>()
        var start = newMoonOnOrBefore(from)
        while (start <= to) {
            val end = nextNewMoon(start)
            months += monthFrom(start, end)
            start = end
        }
        return months
    }

    // A month takes its name from the Sun's sign at its opening new moon. With no sankranti
    // before the next new moon, the Sun is still in that sign and this month is the adhika one.
    private fun monthFrom(start: LocalDateTime, end: LocalDateTime): LunarMonth {
        val sign = sunSign(start)
        return LunarMonth(
            index = (sign + 1) % 12,
            isAdhika = sign == sunSign(end),
            start = start,
            end = end
        )
    }

    fun sunriseAt(date: LocalDate, lat: Double, lon: Double): LocalDateTime =
        LocalDateTime.of(date, AstronomyEngine.computeSunMoonTimes(date, lat, lon).sunrise)

    fun sunsetAt(date: LocalDate, lat: Double, lon: Double): LocalDateTime =
        LocalDateTime.of(date, AstronomyEngine.computeSunMoonTimes(date, lat, lon).sunset)

    fun tithiAt(time: LocalDateTime): Int = (floor(elongation(time) / TITHI_SPAN).toInt() + 1).coerceIn(1, 30)

    /** Sidereal (Lahiri) sign of the Sun, 0 = Mesha … 11 = Meena. */
    fun sunSign(time: LocalDateTime): Int =
        floor(AstronomyEngine.getCelestialPositions(time).siderealSun / 30.0).toInt().coerceIn(0, 11)

    fun newMoonOnOrBefore(time: LocalDateTime): LocalDateTime {
        var newMoon = elongationCrossing(time.minusDays(31), 0.0)
        while (true) {
            val next = nextNewMoon(newMoon)
            if (next > time) return newMoon
            newMoon = next
        }
    }

    fun nextNewMoon(after: LocalDateTime): LocalDateTime = elongationCrossing(after.plusHours(1), 0.0)

    /** Start and end of tithi [tithi] (1–30) inside [month]. */
    fun tithiInterval(month: LunarMonth, tithi: Int): Pair<LocalDateTime, LocalDateTime> {
        val start = if (tithi == 1) month.start else elongationCrossing(month.start, TITHI_SPAN * (tithi - 1))
        val end = if (tithi == 30) month.end else elongationCrossing(month.start, TITHI_SPAN * tithi)
        return start to end
    }

    /** Moments when the Sun enters a new sidereal sign within [from, to]. */
    fun sankrantis(from: LocalDateTime, to: LocalDateTime): List<Pair<LocalDateTime, Int>> {
        val result = mutableListOf<Pair<LocalDateTime, Int>>()
        var before = from
        var sign = sunSign(before)
        while (before < to) {
            val after = before.plusDays(1)
            val nextSign = sunSign(after)
            if (nextSign != sign) {
                val enters = bisect(before, after) { sunSign(it) == nextSign }
                result += enters to nextSign
                sign = nextSign
            }
            before = after
        }
        return result
    }

    private fun elongation(time: LocalDateTime): Double {
        val positions = AstronomyEngine.getCelestialPositions(time)
        return normalize(positions.siderealMoon - positions.siderealSun)
    }

    /** First moment after [from] when the Moon–Sun elongation reaches [target] degrees. */
    private fun elongationCrossing(from: LocalDateTime, target: Double): LocalDateTime {
        val offset = { t: LocalDateTime -> wrap180(elongation(t) - target) }
        var before = from
        var offsetBefore = offset(before)
        repeat(4 * 35) {
            val after = before.plus(MOON_STEP)
            val offsetAfter = offset(after)
            // The jump at ±180° is the far side of the cycle, not a crossing.
            if (offsetBefore < 0 && offsetAfter >= 0 && offsetAfter - offsetBefore < 90) {
                return bisect(before, after) { offset(it) >= 0 }
            }
            before = after
            offsetBefore = offsetAfter
        }
        error("No elongation crossing of $target° within 35 days of $from")
    }

    /** Earliest time in (before, after] where [reached] turns true, to within 30 seconds. */
    private fun bisect(
        before: LocalDateTime,
        after: LocalDateTime,
        reached: (LocalDateTime) -> Boolean
    ): LocalDateTime {
        var low = before.toEpochSecond(ZoneOffset.UTC)
        var high = after.toEpochSecond(ZoneOffset.UTC)
        while (high - low > 30) {
            val mid = low + (high - low) / 2
            if (reached(LocalDateTime.ofEpochSecond(mid, 0, ZoneOffset.UTC))) high = mid else low = mid
        }
        return LocalDateTime.ofEpochSecond(high, 0, ZoneOffset.UTC)
    }

    private fun normalize(degrees: Double): Double = ((degrees % 360.0) + 360.0) % 360.0

    private fun wrap180(degrees: Double): Double = normalize(degrees + 180.0) - 180.0
}
