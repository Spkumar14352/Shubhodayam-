package com.example.widget

import android.content.Context
import com.example.data.festivals.FestivalCalendar
import com.example.data.model.CityData
import com.example.data.model.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WidgetUpdaterTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val city = CityData.defaultCity
    private val hourMinute = Regex("""\d{2}:\d{2}""")
    private val today = LocalDate.of(2026, 11, 9)

    @Test
    fun savesTodayAndTomorrowWithExpectedFormats() {
        val json = WidgetUpdater.buildJson(context, today, Language.TELUGU, city)
        val tomorrow = json.getJSONObject("tomorrow")

        assertEquals("2026-11-09", json.getString("date"))
        assertEquals("2026-11-10", tomorrow.getString("date"))
        assertEquals("te", json.getString("lang"))

        listOf(json, tomorrow).forEach { day ->
            listOf("tithiEnds", "nakshatraEnds", "sunrise", "sunset").forEach { field ->
                assertTrue(field, hourMinute.matches(day.getString(field)))
            }
            assertTrue(Regex("""\d{2}:\d{2} - \d{2}:\d{2}""").matches(day.getString("rahuKalam")))
            assertTrue(day.getString("vaaram").isNotBlank())
            assertTrue(day.getString("tithi").isNotBlank())
            assertTrue(day.getString("nakshatra").isNotBlank())
        }
    }

    @Test
    fun usesSelectedLanguage() {
        val json = WidgetUpdater.buildJson(context, today, Language.ENGLISH, city)

        assertEquals("en", json.getString("lang"))
        assertTrue(json.getString("vaaram").startsWith("Monday"))
    }

    @Test
    fun festivalIsFilledOnlyOnFestivalDates() {
        val festivals = FestivalCalendar.forYear(context, 2026, city.latitude, city.longitude)
        val festival = festivals.first { it.isMajor }
        val json = WidgetUpdater.buildJson(context, festival.date, Language.ENGLISH, city)

        assertEquals(festival.nameEn, json.getString("festival"))

        val dates = festivals.map { it.date }.toSet()
        val plainDay = generateSequence(festival.date) { it.plusDays(1) }.first { it !in dates }
        val plain = WidgetUpdater.buildJson(context, plainDay, Language.ENGLISH, city)
        assertEquals("", plain.getString("festival"))
    }
}
