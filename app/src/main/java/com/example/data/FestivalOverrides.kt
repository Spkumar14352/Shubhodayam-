package com.example.data

import java.time.LocalDate

data class FestivalOverrideEntry(
    val date: LocalDate,
    val isVerified: Boolean = true,
    val source: String = "ధృవీకరించబడిన పంచాంగం (Verified Telugu Panchangam)"
)

/**
 * Verified festival date overrides for accurate Telugu calendar representation.
 * Override dates always win over calculated dates and display a gold "Verified" badge.
 */
object FestivalOverrides {

    private val overrides = mapOf<Pair<String, Int>, FestivalOverrideEntry>(
        Pair("karthika_masam_start", 2026) to FestivalOverrideEntry(LocalDate.of(2026, 11, 10)),
        Pair("karthika_pournami", 2026) to FestivalOverrideEntry(LocalDate.of(2026, 11, 24)),
        Pair("karthika_amavasya", 2026) to FestivalOverrideEntry(LocalDate.of(2026, 12, 8)),
        Pair("deepavali", 2026) to FestivalOverrideEntry(LocalDate.of(2026, 11, 8))
    )

    fun get(festivalId: String, year: Int): FestivalOverrideEntry? = overrides[Pair(festivalId, year)]

    fun has(festivalId: String, year: Int): Boolean = overrides.containsKey(Pair(festivalId, year))

    fun all(): Map<Pair<String, Int>, FestivalOverrideEntry> = overrides
}
