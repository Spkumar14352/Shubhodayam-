package com.example.data.model

import com.example.data.AstronomyEngine
import java.time.LocalDateTime
import kotlin.math.floor

enum class BabyGender { BOY, GIRL, UNISEX }

data class BabyName(
    val name: String,
    val telugu: String,
    val gender: BabyGender,
    val meaningEn: String,
    val meaningTe: String,
    val syllable: String,
    val tags: List<String>
)

data class PadaSyllable(val en: String, val te: String)

data class NakshatraSyllables(val nakshatraNumber: Int, val padas: List<PadaSyllable>)

/** A name saved to "My shortlist". AI names keep one meaning, in the language they were asked in. */
data class ShortlistedName(
    val name: String,
    val telugu: String,
    val meaningEn: String,
    val meaningTe: String,
    val isAiSuggested: Boolean
)

/** The nakshatra to find names for. [pada] is null when unknown; [rasi] is set for birth details. */
data class BirthStar(
    val nakshatraNumber: Int,
    val pada: Int? = null,
    val rasi: Rasi? = null
)

data class BabyNameFilters(
    val gender: BabyGender? = null,
    val syllable: String? = null,
    val tags: Set<String> = emptySet(),
    val shortNamesOnly: Boolean = false
)

object BabyNameSearch {
    const val SHORT_NAME_MAX_LETTERS = 5

    private const val NAKSHATRA_SPAN = 360.0 / 27.0
    private const val PADA_SPAN = NAKSHATRA_SPAN / 4.0
    private const val RASI_SPAN = 30.0

    fun birthStarAt(kolkataDateTime: LocalDateTime): BirthStar =
        birthStarFromMoon(AstronomyEngine.getCelestialPositions(kolkataDateTime).siderealMoon)

    fun birthStarFromMoon(siderealMoon: Double): BirthStar {
        val padaIndex = floor((siderealMoon % NAKSHATRA_SPAN) / PADA_SPAN).toInt().coerceIn(0, 3)
        val rasiIndex = floor(siderealMoon / RASI_SPAN).toInt().coerceIn(0, 11)
        return BirthStar(
            nakshatraNumber = AstronomyEngine.getNakshatraNumber(siderealMoon),
            pada = padaIndex + 1,
            rasi = RasiData.allRasis[rasiIndex]
        )
    }

    fun syllablesFor(star: BirthStar): List<PadaSyllable> =
        NakshatraSyllablesData.forNakshatra(star.nakshatraNumber).padas.distinctBy { it.en }

    fun namakshara(star: BirthStar): PadaSyllable? =
        star.pada?.let { NakshatraSyllablesData.forNakshatra(star.nakshatraNumber).padas[it - 1] }

    fun search(star: BirthStar, filters: BabyNameFilters): List<BabyName> {
        val syllables = filters.syllable?.let(::setOf)
            ?: syllablesFor(star).map { it.en }.toSet()

        return BabyNamesData.all
            .filter { it.syllable in syllables }
            .filter { filters.gender == null || it.gender == filters.gender }
            .filter { filters.tags.isEmpty() || it.tags.any(filters.tags::contains) }
            .filter { !filters.shortNamesOnly || it.name.length <= SHORT_NAME_MAX_LETTERS }
            .sortedBy { it.name }
    }
}
