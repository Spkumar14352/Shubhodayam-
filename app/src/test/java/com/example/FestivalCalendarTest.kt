package com.example

import com.example.data.LunarCalendar
import com.example.data.festivals.FestivalCalculator
import com.example.data.festivals.FestivalData
import com.example.data.model.CityData
import com.example.data.model.FestivalCalendarItem
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
 * Validates calculated Telugu festivals and asserts verified 2026 dates without overrides.
 * Prints formatted table of festivals from 2026-11-01 to 2027-12-31 for comparison with
 * printed Telugu panchangams.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FestivalCalendarTest {
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

    // Pure calculation without overrides
    private val rawCalculator = FestivalCalculator(load(overridesTs = "export const festivalOverrides = [];"))
    // With overrides
    private val standardCalculator = FestivalCalculator(load())

    private fun rawFestivals(year: Int): List<FestivalCalendarItem> =
        rawCalculator.calculate(year, hyderabad.latitude, hyderabad.longitude, applyOverrides = false)

    private fun standardFestivals(year: Int): List<FestivalCalendarItem> =
        standardCalculator.calculate(year, hyderabad.latitude, hyderabad.longitude, applyOverrides = true)

    @Test
    fun assertFourVerifiedDatesProducedByCalculationWithoutOverrides() {
        val calculated2026 = rawFestivals(2026)

        // 1. Karthika Masam start: Kartika Shukla Pratipada -> 2026-11-10
        val karthikaStart = calculated2026.firstOrNull { it.baseId == "karthika_masam_start" }
        assertNotNull("karthika_masam_start must be calculated", karthikaStart)
        assertEquals("karthika_masam_start date", LocalDate.of(2026, 11, 10), karthikaStart!!.date)

        // 2. Karthika Pournami: Kartika Purnima -> 2026-11-24
        val karthikaPournami = calculated2026.firstOrNull { it.baseId == "karthika_pournami" }
        assertNotNull("karthika_pournami must be calculated", karthikaPournami)
        assertEquals("karthika_pournami date", LocalDate.of(2026, 11, 24), karthikaPournami!!.date)

        // 3. Karthika Amavasya: Kartika Amavasya (month end) -> 2026-12-08
        val karthikaAmavasya = calculated2026.firstOrNull { it.baseId == "karthika_amavasya" }
        assertNotNull("karthika_amavasya must be calculated", karthikaAmavasya)
        assertEquals("karthika_amavasya date", LocalDate.of(2026, 12, 8), karthikaAmavasya!!.date)

        // 4. Deepavali: Ashwayuja Amavasya -> 2026-11-08
        val deepavali = calculated2026.firstOrNull { it.baseId == "deepavali" }
        assertNotNull("deepavali must be calculated", deepavali)
        assertEquals("deepavali date", LocalDate.of(2026, 11, 8), deepavali!!.date)
    }

    @Test
    fun assertCalculatedKarthikaFestivals() {
        val calculated2026 = rawFestivals(2026)

        // Bhagini Hasta Bhojanam -> 2026-11-11
        val bhagini = calculated2026.firstOrNull { it.baseId == "bhagini_hasta_bhojanam" }
        assertNotNull("Bhagini Hasta Bhojanam must be present", bhagini)
        assertEquals(LocalDate.of(2026, 11, 11), bhagini!!.date)

        // Nagula Chavithi -> 2026-11-13
        val nagula = calculated2026.firstOrNull { it.baseId == "nagula_chavithi" }
        assertNotNull("Nagula Chavithi must be present", nagula)
        assertEquals(LocalDate.of(2026, 11, 13), nagula!!.date)

        // Utthana Ekadashi -> 2026-11-21
        val utthana = calculated2026.firstOrNull { it.baseId == "utthana_ekadashi" }
        assertNotNull("Utthana Ekadashi must be present", utthana)
        assertEquals(LocalDate.of(2026, 11, 21), utthana!!.date)

        // Ksheerabdi Dwadashi -> 2026-11-21
        val ksheerabdi = calculated2026.firstOrNull { it.baseId == "ksheerabdi_dwadashi" }
        assertNotNull("Ksheerabdi Dwadashi must be present", ksheerabdi)
        assertEquals(LocalDate.of(2026, 11, 21), ksheerabdi!!.date)

        // Karthika Somavarams (1st, 2nd, 3rd, 4th)
        val somavaram1 = calculated2026.firstOrNull { it.baseId == "karthika_somavaram_1" }
        val somavaram2 = calculated2026.firstOrNull { it.baseId == "karthika_somavaram_2" }
        val somavaram3 = calculated2026.firstOrNull { it.baseId == "karthika_somavaram_3" }
        val somavaram4 = calculated2026.firstOrNull { it.baseId == "karthika_somavaram_4" }

        assertNotNull("1st Karthika Somavaram must exist", somavaram1)
        assertNotNull("2nd Karthika Somavaram must exist", somavaram2)
        assertNotNull("3rd Karthika Somavaram must exist", somavaram3)
        assertNotNull("4th Karthika Somavaram must exist", somavaram4)

        assertEquals(LocalDate.of(2026, 11, 16), somavaram1!!.date)
        assertEquals(LocalDate.of(2026, 11, 23), somavaram2!!.date)
        assertEquals(LocalDate.of(2026, 11, 30), somavaram3!!.date)
        assertEquals(LocalDate.of(2026, 12, 7), somavaram4!!.date)

        assertTrue(somavaram1.nameTe.contains("1వ కార్తీక సోమవారం"))
        assertTrue(somavaram2.nameTe.contains("2వ కార్తీక సోమవారం"))
        assertTrue(somavaram3.nameTe.contains("3వ కార్తీక సోమవారం"))
        assertTrue(somavaram4.nameTe.contains("4వ కార్తీక సోమవారం"))
    }

    @Test
    fun assertMonthBeginsEntriesExist() {
        val calculated2026 = rawFestivals(2026)
        val margashiraStart = calculated2026.firstOrNull { it.baseId == "margashira_masam_start" }
        assertNotNull("Margashira Masam start must exist", margashiraStart)
        assertTrue(margashiraStart!!.nameTe.contains("మార్గశిర మాసారంభం"))
    }

    @Test
    fun assertMakaraSankrantiTrioSeparateEntries() {
        val calculated2026 = rawFestivals(2026)
        val bhogi = calculated2026.single { it.baseId == "bhogi" }
        val sankranti = calculated2026.single { it.baseId == "makara_sankranti" }
        val kanuma = calculated2026.single { it.baseId == "kanuma" }

        assertEquals(sankranti.date.minusDays(1), bhogi.date)
        assertEquals(sankranti.date.plusDays(1), kanuma.date)
        assertTrue(bhogi.isMajor)
        assertTrue(sankranti.isMajor)
        assertTrue(kanuma.isMajor)
    }

    @Test
    fun assertOverridesShowVerifiedBadge() {
        val standard2026 = standardFestivals(2026)
        val verifiedIds = listOf("karthika_masam_start", "karthika_pournami", "karthika_amavasya", "deepavali")
        for (id in verifiedIds) {
            val item = standard2026.firstOrNull { it.baseId == id }
            assertNotNull("Item $id must exist", item)
            assertTrue("Item $id must be marked isVerified", item!!.isVerified)
        }
    }

    @Test
    fun printCalculatedFestivalsTableFromNov2026ToDec2027() {
        val weekdayFmt = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
        val startRange = LocalDate.of(2026, 11, 1)
        val endRange = LocalDate.of(2027, 12, 31)

        val allItems = (rawFestivals(2026) + rawFestivals(2027))
            .filter { !it.date.isBefore(startRange) && !it.date.isAfter(endRange) }
            .sortedWith(compareBy<FestivalCalendarItem> { it.date }.thenBy { !it.isMajor })

        val tableHeader = String.format(
            "%-12s | %-4s | %-38s | %-30s | %-16s",
            "Date", "Day", "Festival Name (Telugu & English)", "Tithi at Sunrise", "Lunar Month"
        )
        val divider = "-".repeat(110)

        val tableReport = buildString {
            appendLine("==============================================================================================================")
            appendLine("TELUGU FESTIVAL CALENDAR TABLE: 2026-11-01 to 2027-12-31")
            appendLine("Calculated using sunrise in Asia/Kolkata for ${hyderabad.nameEn} (${hyderabad.latitude}, ${hyderabad.longitude})")
            appendLine("==============================================================================================================")
            appendLine(tableHeader)
            appendLine(divider)

            for (f in allItems) {
                val lunarDay = LunarCalendar.getLunarMonth(f.date, hyderabad.latitude, hyderabad.longitude)
                val monthName = "${lunarDay.month.nameTe} (${lunarDay.month.nameEn})"
                val nameCombined = "${f.nameTe} / ${f.nameEn}"
                val trimmedName = if (nameCombined.length > 38) nameCombined.take(35) + "..." else nameCombined
                val tithiCombined = if (f.tithiTe.isNotBlank()) f.tithiTe else f.tithiEn
                val trimmedTithi = if (tithiCombined.length > 30) tithiCombined.take(27) + "..." else tithiCombined

                appendLine(
                    String.format(
                        "%-12s | %-4s | %-38s | %-30s | %-16s",
                        f.date.toString(),
                        f.date.format(weekdayFmt),
                        trimmedName,
                        trimmedTithi,
                        monthName
                    )
                )
            }
            appendLine(divider)
            appendLine("Total calculated festival entries: ${allItems.size}")
            appendLine("==============================================================================================================")
        }

        println(tableReport)

        // Write to build/reports/festival-calendar-table.txt
        val reportFile = File("build/reports/festival-calendar-table.txt")
        reportFile.parentFile?.mkdirs()
        reportFile.writeText(tableReport)
        assertTrue("Report file must be created", reportFile.exists())
    }
}
