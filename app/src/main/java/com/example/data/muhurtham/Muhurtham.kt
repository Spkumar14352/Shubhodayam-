package com.example.data.muhurtham

import android.content.Context
import com.example.data.AstronomyEngine
import com.example.data.RealPanchangCalculation
import com.example.data.festivals.FestivalData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

enum class MuhurthamType(val key: String, val nameTe: String) {
    MARRIAGE("Marriage", "వివాహం"),
    GRIHA_PRAVESAM("Griha Pravesam", "గృహ ప్రవేశం"),
    VEHICLE("Vehicle", "వాహనం"),
    UPANAYANAM("Upanayanam", "ఉపనయనం"),
    ANNAPRASANA("Annaprasana", "అన్నప్రాశన"),
    NAMING("Naming", "నామకరణం");

    companion object {
        fun fromKey(key: String): MuhurthamType =
            entries.firstOrNull { it.key == key } ?: error("Unknown muhurtham type \"$key\"")
    }
}

data class MuhurthaRule(
    val type: MuhurthamType,
    val avoidTithis: Set<Int>,
    val avoidKrishnaAfterAshtami: Boolean,
    val nakshatras: Set<Int>,
    val avoidWeekdays: Set<DayOfWeek>
) {
    fun allows(tithi: Int, nakshatra: Int, weekday: DayOfWeek): Boolean =
        tithi !in avoidTithis &&
            !(avoidKrishnaAfterAshtami && tithi > KRISHNA_ASHTAMI) &&
            nakshatra in nakshatras &&
            weekday !in avoidWeekdays

    private companion object {
        const val KRISHNA_ASHTAMI = 23
    }
}

data class VerifiedMuhurtham(
    val date: LocalDate,
    val type: MuhurthamType,
    val start: LocalTime?,
    val end: LocalTime?,
    val lagnam: String,
    val notes: String
)

/** A suggested or verified day. Tithi (1–30) and nakshatra (1–27) are at sunrise. */
data class MuhurthamDay(
    val date: LocalDate,
    val type: MuhurthamType,
    val tithi: Int,
    val nakshatra: Int,
    val windowStart: LocalTime?,
    val windowEnd: LocalTime?,
    val isAbhijit: Boolean,
    val isVerified: Boolean,
    val lagnam: String = "",
    val notes: String = ""
)

data class MuhurthamData(val rules: List<MuhurthaRule>, val verified: List<VerifiedMuhurtham>) {
    companion object {
        const val RULES_FILE = "muhurthaRules.ts"
        const val VERIFIED_FILE = "muhurthams.ts"

        private val NAKSHATRAS = listOf(
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu", "Pushya", "Ashlesha",
            "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
            "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada",
            "Uttara Bhadrapada", "Revati"
        )
        private val ALIASES = mapOf(
            "Pushyami" to "Pushya", "Moola" to "Mula", "Sravana" to "Shravana", "Shravanam" to "Shravana",
            "Dhanishtha" to "Dhanishta", "Chitta" to "Chitra", "Swathi" to "Swati", "Visakha" to "Vishakha"
        )

        fun fromAssets(context: Context): MuhurthamData {
            fun read(name: String) =
                context.assets.open("${FestivalData.ASSET_DIR}/$name").bufferedReader().use { it.readText() }
            return parse(read(RULES_FILE), read(VERIFIED_FILE))
        }

        fun parse(rulesTs: String, verifiedTs: String) = MuhurthamData(
            rules = FestivalData.objects(rulesTs).map { json ->
                fun list(key: String) = json.optJSONArray(key)?.let { array -> (0 until array.length()).map { array.get(it) } }.orEmpty()
                MuhurthaRule(
                    type = MuhurthamType.fromKey(json.getString("type")),
                    avoidTithis = list("avoidTithis").map { (it as Number).toInt() }.toSet(),
                    avoidKrishnaAfterAshtami = json.optBoolean("avoidKrishnaAfterAshtami"),
                    nakshatras = list("nakshatras").map { nakshatraNumber(it.toString()) }.toSet(),
                    avoidWeekdays = list("avoidWeekdays").map { DayOfWeek.valueOf(it.toString().uppercase()) }.toSet()
                )
            },
            verified = FestivalData.objects(verifiedTs).map { json ->
                VerifiedMuhurtham(
                    date = LocalDate.parse(json.getString("date")),
                    type = MuhurthamType.fromKey(json.getString("type")),
                    start = json.optString("start").takeIf { it.isNotEmpty() }?.let(LocalTime::parse),
                    end = json.optString("end").takeIf { it.isNotEmpty() }?.let(LocalTime::parse),
                    lagnam = json.optString("lagnam"),
                    notes = json.optString("notes")
                )
            }
        )

        private fun nakshatraNumber(name: String): Int {
            val index = NAKSHATRAS.indexOf(ALIASES[name] ?: name)
            require(index >= 0) { "Unknown nakshatra \"$name\" in ${RULES_FILE}" }
            return index + 1
        }
    }
}

/** Finds generally good days with the Panchang engine and merges in verified dates. */
class MuhurthamFinder(private val data: MuhurthamData) {

    fun find(from: LocalDate, days: Int, lat: Double, lon: Double): Map<MuhurthamType, List<MuhurthamDay>> {
        val found = MuhurthamType.entries.associateWith { mutableListOf<MuhurthamDay>() }

        for (offset in 0 until days) {
            val date = from.plusDays(offset.toLong())
            val panchang = AstronomyEngine.calculatePanchang(date, lat, lon)
            data.rules
                .filter { it.allows(panchang.tithiNumber, panchang.nakshatraNumber, date.dayOfWeek) }
                .forEach { rule ->
                    val (start, end, isAbhijit) = goodWindow(panchang)
                    found.getValue(rule.type) += MuhurthamDay(
                        date, rule.type, panchang.tithiNumber, panchang.nakshatraNumber, start, end, isAbhijit, isVerified = false
                    )
                }
        }

        // A verified entry replaces the suggestion for its day and type.
        data.verified.filter { !it.date.isBefore(from) }.forEach { verified ->
            val panchang = AstronomyEngine.calculatePanchang(verified.date, lat, lon)
            val list = found.getValue(verified.type)
            list.removeAll { it.date == verified.date }
            list += MuhurthamDay(
                date = verified.date,
                type = verified.type,
                tithi = panchang.tithiNumber,
                nakshatra = panchang.nakshatraNumber,
                windowStart = verified.start,
                windowEnd = verified.end,
                isAbhijit = false,
                isVerified = true,
                lagnam = verified.lagnam,
                notes = verified.notes
            )
        }

        return found.mapValues { (_, list) -> list.sortedBy { it.date } }
    }

    /** Abhijit when it is clear of Rahu Kalam and Yamagandam; otherwise the longest clear daytime stretch. */
    internal fun goodWindow(panchang: RealPanchangCalculation): Triple<LocalTime, LocalTime, Boolean> {
        val avoid = listOf(panchang.rahuKalam, panchang.yamagandam)
            .map { it.startTime to it.endTime }
            .sortedBy { it.first }
        val abhijit = panchang.abhijitMuhurtham
        if (avoid.none { (start, end) -> abhijit.startTime < end && start < abhijit.endTime }) {
            return Triple(abhijit.startTime, abhijit.endTime, true)
        }

        var best = panchang.sunMoonTimes.sunrise to panchang.sunMoonTimes.sunrise
        var cursor = panchang.sunMoonTimes.sunrise
        for ((start, end) in avoid + (panchang.sunMoonTimes.sunset to panchang.sunMoonTimes.sunset)) {
            if (start > cursor && length(cursor, start) > length(best.first, best.second)) best = cursor to start
            if (end > cursor) cursor = end
        }
        return Triple(best.first, best.second, false)
    }

    private fun length(start: LocalTime, end: LocalTime) = end.toSecondOfDay() - start.toSecondOfDay()
}

/** Muhurthams for the next 12 months at the user's city, cached per day and place. */
object MuhurthamCalendar {
    const val DAYS_AHEAD = 365
    private var finder: MuhurthamFinder? = null
    private var cacheKey: Triple<LocalDate, Double, Double>? = null
    private var cached: Map<MuhurthamType, List<MuhurthamDay>> = emptyMap()

    @Synchronized
    fun get(context: Context, from: LocalDate, lat: Double, lon: Double): Map<MuhurthamType, List<MuhurthamDay>> {
        val key = Triple(from, lat, lon)
        if (key != cacheKey) {
            val f = finder ?: MuhurthamFinder(MuhurthamData.fromAssets(context.applicationContext)).also { finder = it }
            cached = f.find(from, DAYS_AHEAD, lat, lon)
            cacheKey = key
        }
        return cached
    }
}
