package com.example.data.festivals

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate

enum class Kala { SUNRISE, MADHYAHNA, APARAHNA, PRADOSHA, NISHITA }

enum class Paksha { SHUKLA, KRISHNA, BOTH }

data class FestivalText(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val descriptionEn: String = "",
    val descriptionTe: String = "",
    val ritualsEn: String = "",
    val ritualsTe: String = ""
)

data class FestivalOffset(val days: Long, val text: FestivalText)

data class FestivalRule(
    val text: FestivalText,
    val type: String,
    val month: Int?,
    val paksha: Paksha,
    val tithi: Int,
    val kala: Kala,
    val weekday: DayOfWeek?,
    val sunSign: Int?,
    val sign: Int?,
    val major: Boolean,
    val offsets: List<FestivalOffset>
) {
    /** Tithi number 1–30 for a single paksha. */
    fun absoluteTithi(paksha: Paksha = this.paksha): Int = if (paksha == Paksha.KRISHNA) 15 + tithi else tithi
}

data class FixedFestival(
    val text: FestivalText,
    val monthDay: String?,
    val dates: List<LocalDate>,
    val moonSighting: Boolean,
    val major: Boolean
)

data class FestivalOverride(val id: String, val year: Int, val date: LocalDate, val source: String)

/** The three editable files in /data, which the build copies into the app's assets. */
data class FestivalData(
    val rules: List<FestivalRule>,
    val fixed: List<FixedFestival>,
    val overrides: List<FestivalOverride>
) {
    companion object {
        const val RULES_FILE = "festivalRules.ts"
        const val FIXED_FILE = "fixedFestivals.ts"
        const val OVERRIDES_FILE = "festivalOverrides.ts"
        const val ASSET_DIR = "app_data"

        private val MONTHS = listOf(
            "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha", "Shravana", "Bhadrapada",
            "Ashwayuja", "Kartika", "Margashira", "Pushya", "Magha", "Phalguna"
        )
        private val SIGNS = listOf(
            "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
            "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena"
        )

        fun fromAssets(context: Context): FestivalData {
            fun read(name: String) = context.assets.open("$ASSET_DIR/$name").bufferedReader().use { it.readText() }
            return parse(read(RULES_FILE), read(FIXED_FILE), read(OVERRIDES_FILE))
        }

        fun parse(rulesTs: String, fixedTs: String, overridesTs: String) = FestivalData(
            rules = objects(rulesTs).map(::parseRule),
            fixed = objects(fixedTs).map(::parseFixed),
            overrides = objects(overridesTs).map {
                FestivalOverride(
                    id = it.getString("id"),
                    year = it.getInt("year"),
                    date = LocalDate.parse(it.getString("date")),
                    source = it.optString("source")
                )
            }
        )

        /** The array after `export const x: T[] =`, read with org.json's lenient parser (allows comments). */
        internal fun objects(typescript: String): List<JSONObject> {
            val declaration = typescript.indexOf("export const")
            val equals = typescript.indexOf('=', declaration)
            val start = typescript.indexOf('[', equals)
            val end = typescript.lastIndexOf(']')
            require(declaration >= 0 && equals > 0 && start > 0 && end > start) { "No exported array found" }
            val array = JSONArray(typescript.substring(start, end + 1))
            // A trailing comma reads as a null entry; skip those.
            return (0 until array.length()).mapNotNull(array::optJSONObject)
        }

        private fun parseText(json: JSONObject) = FestivalText(
            id = json.getString("id"),
            nameEn = json.getString("nameEn"),
            nameTe = json.getString("nameTe"),
            descriptionEn = json.optString("descriptionEn"),
            descriptionTe = json.optString("descriptionTe"),
            ritualsEn = json.optString("ritualsEn"),
            ritualsTe = json.optString("ritualsTe")
        )

        private fun parseRule(json: JSONObject): FestivalRule {
            fun index(list: List<String>, key: String): Int? = json.optString(key).takeIf { it.isNotEmpty() }?.let {
                list.indexOf(it).also { found -> require(found >= 0) { "Unknown $key \"$it\" in ${json.optString("id")}" } }
            }
            val offsets = json.optJSONArray("offsets")
            return FestivalRule(
                text = parseText(json),
                type = json.getString("type"),
                month = index(MONTHS, "month"),
                paksha = when (json.optString("paksha", "shukla")) {
                    "krishna" -> Paksha.KRISHNA
                    "both" -> Paksha.BOTH
                    else -> Paksha.SHUKLA
                },
                tithi = json.optInt("tithi", 1),
                kala = Kala.valueOf(json.optString("kala", "sunrise").uppercase()),
                weekday = json.optString("weekday").takeIf { it.isNotEmpty() }?.let { DayOfWeek.valueOf(it.uppercase()) },
                sunSign = index(SIGNS, "sunSign"),
                sign = index(SIGNS, "sign"),
                major = json.optBoolean("major", true),
                offsets = (0 until (offsets?.length() ?: 0)).map { i ->
                    val offset = offsets!!.getJSONObject(i)
                    FestivalOffset(offset.getLong("days"), parseText(offset))
                }
            )
        }

        private fun parseFixed(json: JSONObject): FixedFestival {
            val dates = json.optJSONArray("dates")
            return FixedFestival(
                text = parseText(json),
                monthDay = json.optString("date").takeIf { it.isNotEmpty() },
                dates = (0 until (dates?.length() ?: 0)).map { LocalDate.parse(dates!!.getString(it)) },
                moonSighting = json.optBoolean("moonSighting"),
                major = json.optBoolean("major", true)
            )
        }
    }
}
