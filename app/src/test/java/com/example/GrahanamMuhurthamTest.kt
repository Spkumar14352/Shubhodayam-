package com.example

import com.example.data.AstronomyEngine
import com.example.data.eclipses.EclipseBody
import com.example.data.eclipses.EclipseCalculator
import com.example.data.eclipses.EclipseConstants
import com.example.data.eclipses.EclipseType
import com.example.data.model.CityData
import com.example.data.model.Language
import com.example.data.muhurtham.MuhurthamData
import com.example.data.muhurtham.MuhurthamFinder
import com.example.data.muhurtham.MuhurthamType
import com.example.services.EclipseReminders
import com.example.utils.CalendarExport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GrahanamMuhurthamTest {
    private val hyderabad = CityData.defaultCity
    private val from = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, EclipseCalculator.IST).toInstant()
    private val eclipses = EclipseCalculator.upcoming(from, hyderabad.latitude, hyderabad.longitude)

    @Test
    fun printsEclipsesFor2026And2027() {
        eclipses.forEach {
            println("${it.nameEn.padEnd(26)} start ${it.start}  peak ${it.peak}  end ${it.end}  visible=${it.isVisible}  sutak=${it.sutakStart}")
        }
        assertTrue(eclipses.isNotEmpty())
        assertTrue(eclipses.zipWithNext().all { (a, b) -> a.start <= b.start })
    }

    @Test
    fun totalLunarEclipseOf3March2026() {
        // Peak 11:33 UTC = 17:03 IST. The Moon rises in Hyderabad before the eclipse ends.
        val eclipse = eclipses.first { it.body == EclipseBody.LUNAR && it.peak.toLocalDate() == LocalDate.of(2026, 3, 3) }

        assertEquals(EclipseType.TOTAL, eclipse.type)
        assertTrue(abs(Duration.between(LocalDateTime.of(2026, 3, 3, 17, 3), eclipse.peak).toMinutes()) <= 5)
        assertTrue(eclipse.isVisible)
        assertEquals(eclipse.start.minusHours(EclipseConstants.LUNAR_SUTAK_HOURS), eclipse.sutakStart)
    }

    @Test
    fun partialLunarEclipseOf28August2026IsBelowTheHorizonInHyderabad() {
        // Peak 04:13 UTC = 09:43 IST, after moonset in India.
        val eclipse = eclipses.first { it.body == EclipseBody.LUNAR && it.peak.toLocalDate() == LocalDate.of(2026, 8, 28) }

        assertEquals(EclipseType.PARTIAL, eclipse.type)
        assertFalse(eclipse.isVisible)
        assertNull(eclipse.sutakStart)
    }

    @Test
    fun remindersComeTheEveningBeforeAndShortlyBeforeSutak() {
        val march = eclipses.first { it.peak.toLocalDate() == LocalDate.of(2026, 3, 3) }
        val sutak = march.sutakStart!!
        val reminders = EclipseReminders.remindersFor(listOf(march), Language.ENGLISH, LocalDateTime.of(2026, 1, 1, 0, 0))

        assertEquals(2, reminders.size)
        assertEquals(sutak.toLocalDate().minusDays(1).atTime(19, 0), reminders[0].at)
        assertEquals(sutak.minusMinutes(30), reminders[1].at)
    }

    private val data = MuhurthamData.parse(
        File(dataDir(), MuhurthamData.RULES_FILE).readText(),
        File(dataDir(), MuhurthamData.VERIFIED_FILE).readText()
    )

    private fun dataDir() = listOf(File("../data"), File("data")).first { File(it, MuhurthamData.RULES_FILE).exists() }

    @Test
    fun marriageDaysFollowEveryRule() {
        val start = LocalDate.of(2026, 11, 1)
        val days = MuhurthamFinder(data).find(start, 120, hyderabad.latitude, hyderabad.longitude)
        val marriage = days.getValue(MuhurthamType.MARRIAGE)
        val allowedNakshatras = setOf(4, 5, 10, 12, 13, 15, 17, 19, 21, 26, 27)

        assertTrue(marriage.isNotEmpty())
        marriage.forEach { day ->
            assertTrue("${day.date}", day.tithi !in setOf(4, 9, 14, 19, 24, 29, 30) && day.tithi <= 23)
            assertTrue("${day.date}", day.nakshatra in allowedNakshatras)
            assertTrue("${day.date}", day.date.dayOfWeek !in setOf(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY))

            val panchang = AstronomyEngine.calculatePanchang(day.date, hyderabad.latitude, hyderabad.longitude)
            listOf(panchang.rahuKalam, panchang.yamagandam).forEach { bad ->
                assertFalse("${day.date} window overlaps ${bad.nameEn}", day.windowStart!! < bad.endTime && bad.startTime < day.windowEnd!!)
            }
        }
        assertTrue(days.getValue(MuhurthamType.NAMING).isEmpty())
    }

    @Test
    fun verifiedEntriesReplaceSuggestions() {
        val verified = """
            export const muhurthams = [
              { "date": "2026-12-03", "type": "Naming", "start": "10:24", "end": "11:12", "lagnam": "Makara", "notes": "Purohit" }
            ];
        """.trimIndent()
        val withVerified = MuhurthamData.parse(File(dataDir(), MuhurthamData.RULES_FILE).readText(), verified)
        val day = MuhurthamFinder(withVerified)
            .find(LocalDate.of(2026, 12, 1), 10, hyderabad.latitude, hyderabad.longitude)
            .getValue(MuhurthamType.NAMING)
            .single()

        assertTrue(day.isVerified)
        assertEquals(LocalTime.of(10, 24), day.windowStart)
        assertEquals("Makara", day.lagnam)
    }

    @Test
    fun calendarFileUsesUtcTimes() {
        val ics = CalendarExport.ics(
            title = "Marriage, Muhurtham",
            date = LocalDate.of(2026, 12, 3),
            start = LocalTime.of(10, 24),
            end = LocalTime.of(11, 12),
            description = "Line one\nLine two",
            uid = "test"
        )

        assertTrue(ics.contains("DTSTART:20261203T045400Z\r\n"))
        assertTrue(ics.contains("DTEND:20261203T054200Z\r\n"))
        assertTrue(ics.contains("SUMMARY:Marriage\\, Muhurtham\r\n"))
        assertTrue(ics.contains("DESCRIPTION:Line one\\nLine two\r\n"))
        assertNotNull(Regex("BEGIN:VEVENT[\\s\\S]*END:VEVENT").find(ics))
    }
}
