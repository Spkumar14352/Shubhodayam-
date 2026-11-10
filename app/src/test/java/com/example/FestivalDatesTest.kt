package com.example

import com.example.data.LunarCalendar
import com.example.data.festivals.FestivalCalculator
import com.example.data.festivals.FestivalData
import com.example.data.model.CityData
import com.example.data.model.FestivalCalendarItem
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

/**
 * Calculates the festival calendar from the real /data files and prints it for comparison with a
 * printed panchangam. The full table is also written to app/build/reports/festival-dates.txt.
 *
 * The JVM time zone is set to UTC on purpose: results must not depend on the device's zone.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FestivalDatesTest {
    private val hyderabad = CityData.defaultCity

    companion object {
        private val dataDir = listOf(File("../data"), File("data")).first { File(it, FestivalData.RULES_FILE).exists() }
        private var originalZone: TimeZone? = null

        fun load(overridesTs: String? = null) = FestivalData.parse(
            File(dataDir, FestivalData.RULES_FILE).readText(),
            File(dataDir, FestivalData.FIXED_FILE).readText(),
            overridesTs ?: File(dataDir, FestivalData.OVERRIDES_FILE).readText()
        )

        @BeforeClass
        @JvmStatic
        fun useUtc() {
            originalZone = TimeZone.getDefault()
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        }

        @AfterClass
        @JvmStatic
        fun restoreZone() {
            TimeZone.setDefault(originalZone)
        }
    }

    private val calculator = FestivalCalculator(load())

    private fun festivals(year: Int) = calculator.calculate(year, hyderabad.latitude, hyderabad.longitude)

    private fun dateOf(year: Int, baseId: String): LocalDate =
        festivals(year).single { it.baseId == baseId }.date

    @Test
    fun printsCalendarFor2026And2027() {
        val weekday = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
        val report = buildString {
            appendLine("Festival dates for ${hyderabad.nameEn} (${hyderabad.latitude}, ${hyderabad.longitude}), Asia/Kolkata")
            for (year in listOf(2026, 2027)) {
                appendLine()
                appendLine("===== $year =====")
                festivals(year).forEach { f ->
                    val flags = listOfNotNull(
                        "verified".takeIf { f.isVerified },
                        f.noteEn.takeIf { it.isNotEmpty() }
                    ).joinToString(", ")
                    val marker = if (f.isMajor) "*" else " "
                    appendLine(
                        "$marker ${f.date} ${f.date.format(weekday)}  ${f.nameEn.padEnd(42)} ${f.tithiEn}" +
                            if (flags.isEmpty()) "" else "  [$flags]"
                    )
                }
            }
            appendLine()
            appendLine("* = major festival")
        }
        println(report)
        File("build/reports").mkdirs()
        File("build/reports/festival-dates.txt").writeText(report)
    }

    @Test
    fun knownDatesMatch() {
        // New moon 19 Mar 2026 06:53 IST is after Hyderabad sunrise, so Pratipada is skipped at
        // sunrise and Ugadi falls on the day it covers longest.
        assertEquals(LocalDate.of(2026, 3, 19), dateOf(2026, "ugadi"))
        assertEquals(LocalDate.of(2027, 4, 7), dateOf(2027, "ugadi"))
        assertEquals(LocalDate.of(2026, 1, 14), dateOf(2026, "makara_sankranti"))
        // The Sun enters Makara after sunset on 14 Jan 2027.
        assertEquals(LocalDate.of(2027, 1, 15), dateOf(2027, "makara_sankranti"))
        assertEquals(LocalDate.of(2026, 11, 8), dateOf(2026, "deepavali"))
    }

    @Test
    fun relatedDaysFollowTheirFestival() {
        val sankranti = dateOf(2026, "makara_sankranti")
        assertEquals(sankranti.minusDays(1), dateOf(2026, "bhogi"))
        assertEquals(sankranti.plusDays(1), dateOf(2026, "kanuma"))
        assertEquals(dateOf(2026, "bathukamma").plusDays(9), dateOf(2026, "saddula_bathukamma"))

        val varalakshmi = dateOf(2026, "varalakshmi_vratham")
        assertEquals(java.time.DayOfWeek.FRIDAY, varalakshmi.dayOfWeek)
        assertTrue(varalakshmi < dateOf(2026, "raksha_bandhan"))
    }

    @Test
    fun adhikaJyeshtha2026IsSkipped() {
        val months = LunarCalendar.monthsBetween(
            LocalDate.of(2026, 1, 1).atStartOfDay(),
            LocalDate.of(2026, 12, 31).atStartOfDay()
        )
        val adhika = months.filter { it.isAdhika }

        assertEquals(1, adhika.size)
        assertEquals("Jyeshtha", LunarCalendar.MONTH_NAMES_EN[adhika.single().index])
        assertEquals(5, adhika.single().start.monthValue)
    }

    @Test
    fun lunarMonthOnThePanchangScreen() {
        // Amavasya runs until 12:35 IST on 9 Nov 2026, so that morning is still Ashwayuja.
        val nov9 = LunarCalendar.getLunarMonth(LocalDate.of(2026, 11, 9), hyderabad.latitude, hyderabad.longitude)
        assertEquals("Ashwayuja", nov9.month.nameEn)
        assertFalse(nov9.isShukla)

        val nov10 = LunarCalendar.getLunarMonth(LocalDate.of(2026, 11, 10), hyderabad.latitude, hyderabad.longitude)
        assertEquals("Kartika", nov10.month.nameEn)
        assertTrue(nov10.isShukla)
    }

    @Test
    fun everyNamedFestivalOccursOncePerYear() {
        val once = listOf(
            "ugadi", "sri_rama_navami", "akshaya_tritiya", "hanuman_jayanti", "varalakshmi_vratham",
            "raksha_bandhan", "krishnashtami", "vinayaka_chavithi", "bathukamma", "dasara",
            "naraka_chaturdashi", "deepavali", "karthika_pournami", "maha_shivaratri", "holi",
            "makara_sankranti", "bhogi", "kanuma", "republic_day", "independence_day"
        )
        for (year in listOf(2026, 2027)) {
            val all = festivals(year)
            once.forEach { id -> assertEquals("$id in $year", 1, all.count { it.baseId == id }) }
            assertTrue(all.all { it.date.year == year })
            assertEquals(11, all.count { it.baseId == "monthly_sankranti" })
        }
    }

    @Test
    fun overridesWinAndAreMarkedVerified() {
        val overrides = """
            export const festivalOverrides = [
              // trailing comments and unquoted keys are accepted, like hand-edited TypeScript
              { id: "ugadi", year: 2026, date: "2026-03-20", source: "Test holiday list" }
            ];
        """.trimIndent()
        val ugadi: FestivalCalendarItem = FestivalCalculator(load(overrides))
            .calculate(2026, hyderabad.latitude, hyderabad.longitude)
            .single { it.baseId == "ugadi" }

        assertEquals(LocalDate.of(2026, 3, 20), ugadi.date)
        assertTrue(ugadi.isVerified)
        assertEquals("Test holiday list", ugadi.noteEn)
    }

    @Test
    fun moonSightingFestivalsAreFlagged() {
        val ramzan = festivals(2026).single { it.baseId == "ramzan" }
        assertEquals("Subject to moon sighting", ramzan.noteEn)
    }
}
