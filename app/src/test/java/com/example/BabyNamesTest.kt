package com.example

import com.example.data.model.BabyGender
import com.example.data.model.BabyNameFilters
import com.example.data.model.BabyNameSearch
import com.example.data.model.BabyNamesData
import com.example.data.model.BirthStar
import com.example.data.model.NakshatraSyllablesData
import com.example.services.BabyNamesAiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BabyNamesTest {
    private val nakshatraSpan = 360.0 / 27.0
    private val padaSpan = nakshatraSpan / 4.0

    @Test
    fun tableHasEveryNakshatraAndPada() {
        assertEquals(27, NakshatraSyllablesData.all.size)
        assertTrue(NakshatraSyllablesData.all.all { it.padas.size == 4 })
        assertEquals("Chu", NakshatraSyllablesData.forNakshatra(1).padas[0].en)
        assertEquals("Chi", NakshatraSyllablesData.forNakshatra(27).padas[3].en)
    }

    @Test
    fun everyNameIsCompleteAndUsesATableSyllable() {
        val syllables = NakshatraSyllablesData.all.flatMap { it.padas }.map { it.en }.toSet()

        assertTrue(BabyNamesData.all.size >= 300)
        assertEquals(BabyNamesData.all.size, BabyNamesData.all.map { it.name.lowercase() }.toSet().size)
        BabyNamesData.all.forEach {
            assertTrue(it.name, it.syllable in syllables)
            assertTrue(it.name, it.telugu.isNotBlank() && it.meaningEn.isNotBlank() && it.meaningTe.isNotBlank())
            assertTrue(it.name, it.tags.isNotEmpty())
        }
    }

    @Test
    fun padaAndRasiFollowTheMoonLongitude() {
        val start = BabyNameSearch.birthStarFromMoon(0.0)
        assertEquals(1, start.nakshatraNumber)
        assertEquals(1, start.pada)
        assertEquals("mesha", start.rasi?.id)

        val pada2 = BabyNameSearch.birthStarFromMoon(padaSpan + 0.001)
        assertEquals(1, pada2.nakshatraNumber)
        assertEquals(2, pada2.pada)

        val endOfFirst = BabyNameSearch.birthStarFromMoon(nakshatraSpan - 0.0001)
        assertEquals(1, endOfFirst.nakshatraNumber)
        assertEquals(4, endOfFirst.pada)

        val last = BabyNameSearch.birthStarFromMoon(359.999)
        assertEquals(27, last.nakshatraNumber)
        assertEquals(4, last.pada)
        assertEquals("meena", last.rasi?.id)
    }

    @Test
    fun birthTimeUsesThePanchangEngine() {
        // The engine's Panchang has Swati ending at 07:26 IST on 9 Nov 2026.
        val before = BabyNameSearch.birthStarAt(LocalDateTime.of(2026, 11, 9, 7, 0))
        val after = BabyNameSearch.birthStarAt(LocalDateTime.of(2026, 11, 9, 8, 0))

        assertEquals(15, before.nakshatraNumber)
        assertEquals(4, before.pada)
        assertEquals("thula", before.rasi?.id)
        assertEquals(16, after.nakshatraNumber)
        assertEquals(1, after.pada)
    }

    @Test
    fun filtersNarrowResults() {
        val magha = BirthStar(nakshatraNumber = 10)
        val all = BabyNameSearch.search(magha, BabyNameFilters())
        val syllables = setOf("Ma", "Mi", "Mu", "Me")

        assertTrue(all.isNotEmpty())
        assertTrue(all.all { it.syllable in syllables })

        val girlsMa = BabyNameSearch.search(magha, BabyNameFilters(gender = BabyGender.GIRL, syllable = "Ma"))
        assertTrue(girlsMa.isNotEmpty())
        assertTrue(girlsMa.all { it.gender == BabyGender.GIRL && it.syllable == "Ma" })

        val god = BabyNameSearch.search(magha, BabyNameFilters(tags = setOf("God")))
        assertTrue(god.isNotEmpty())
        assertTrue(god.all { "God" in it.tags })

        val short = BabyNameSearch.search(magha, BabyNameFilters(shortNamesOnly = true))
        assertTrue(short.isNotEmpty())
        assertTrue(short.all { it.name.length <= 5 })
    }

    @Test
    fun parsesAiNamesAndDropsBadRows() {
        val names = BabyNamesAiService.parseNames(
            """```json
            [{"name":"Aarav","telugu":"ఆరవ్","meaning":"Peaceful"},
             {"name":"aarav","telugu":"ఆరవ్","meaning":"Duplicate"},
             {"name":"","telugu":"ఆ","meaning":"Missing name"}]
            ```"""
        )

        assertEquals(1, names.size)
        assertEquals("Aarav", names[0].name)
    }

    @Test
    fun aiNamesMustStartWithTheSyllableConsonant() {
        assertTrue(BabyNamesAiService.startsWithSyllableConsonant("Keerthi", "Ki"))
        assertFalse(BabyNamesAiService.startsWithSyllableConsonant("Ravi", "Ki"))
    }
}
