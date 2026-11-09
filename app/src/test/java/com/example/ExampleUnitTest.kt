package com.example

import com.example.data.AstronomyEngine
import com.example.data.PanchangNames
import com.example.data.model.CityData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
    @Test
    fun testPanchangNamesIntegrity() {
        assertEquals(30, PanchangNames.tithis.size)
        assertEquals(27, PanchangNames.nakshatras.size)
        assertEquals(27, PanchangNames.yogas.size)
        assertEquals(7, PanchangNames.repeatingKaranas.size)

        // Check Purnima and Amavasya
        assertEquals("పూర్ణిమ (పౌర్ణమి)", PanchangNames.getTithi(15).nameTe)
        assertEquals("అమావాస్య", PanchangNames.getTithi(30).nameTe)

        // Check Ashwini
        assertEquals("అశ్విని", PanchangNames.getNakshatra(1).nameTe)
        assertEquals("Ashwini", PanchangNames.getNakshatra(1).nameEn)
    }

    @Test
    fun testAstronomyEngineCalculations() {
        val testDate = LocalDate.of(2026, 11, 6)
        val hyd = CityData.defaultCity // Hyderabad

        val panchang = AstronomyEngine.calculatePanchang(testDate, hyd.latitude, hyd.longitude)

        // Tithi within 1..30
        assertTrue("Tithi must be between 1 and 30", panchang.tithiNumber in 1..30)
        assertTrue("Tithi name must not be empty", panchang.tithiTe.isNotBlank())
        assertTrue("Tithi end time must not be empty", panchang.tithiEndTimeStrTe.isNotBlank())

        // Nakshatra within 1..27
        assertTrue("Nakshatra must be between 1 and 27", panchang.nakshatraNumber in 1..27)
        assertTrue("Nakshatra name must not be empty", panchang.nakshatraTe.isNotBlank())

        // Yoga within 1..27
        assertTrue("Yoga must be between 1 and 27", panchang.yogaNumber in 1..27)

        // Karana index within 0..59
        assertTrue("Karana index must be between 0 and 59", panchang.karanaIndex in 0..59)

        // Sunrise and Sunset
        assertNotNull(panchang.sunMoonTimes.sunrise)
        assertNotNull(panchang.sunMoonTimes.sunset)
        assertTrue(panchang.sunMoonTimes.sunrise.isBefore(panchang.sunMoonTimes.sunset))

        // Rahu Kalam and Abhijit Muhurtham
        assertTrue(panchang.rahuKalam.displayStr.isNotBlank())
        assertTrue(panchang.abhijitMuhurtham.displayStr.isNotBlank())
    }

    @Test
    fun testGreetingTemplatesIntegrity() {
        val templates = com.example.data.model.GreetingTemplatesData.templates
        assertTrue("Must have at least 36 templates", templates.size >= 36)

        // Check each category has at least 6 templates
        for (cat in com.example.data.model.GreetingCategory.values()) {
            val count = templates.count { it.category == cat }
            assertTrue("Category ${cat.name} must have at least 6 templates, found $count", count >= 6)
        }

        // Verify the 13 required festivals exist
        val festivalTemplateIds = templates.filter { it.category == com.example.data.model.GreetingCategory.FESTIVALS }.map { it.id }
        val requiredFestivals = listOf(
            "fest_sankranti",
            "fest_ugadi",
            "fest_sri_rama_navami",
            "fest_vinayaka_chavithi",
            "fest_dasara",
            "fest_deepavali",
            "fest_karthika_pournami",
            "fest_maha_shivaratri",
            "fest_varalakshmi_vratham",
            "fest_bathukamma",
            "fest_christmas",
            "fest_ramzan",
            "fest_new_year"
        )
        for (req in requiredFestivals) {
            assertTrue("Required festival template $req must exist", festivalTemplateIds.contains(req))
        }

        // Verify all templates have 5 ready messages and non-empty strings
        for (tmpl in templates) {
            assertTrue("Template ${tmpl.id} must have non-empty titleTe", tmpl.titleTe.isNotBlank())
            assertTrue("Template ${tmpl.id} must have non-empty titleEn", tmpl.titleEn.isNotBlank())
            assertEquals("Template ${tmpl.id} must have 5 readyMessagesTe", 5, tmpl.readyMessagesTe.size)
            assertEquals("Template ${tmpl.id} must have 5 readyMessagesEn", 5, tmpl.readyMessagesEn.size)
        }
    }

    @Test
    fun testGreetingAiServiceFallbackWordCount() = kotlinx.coroutines.runBlocking {
        val aiService = com.example.services.GreetingAiService()
        val template = com.example.data.model.GreetingTemplatesData.templates.first()

        val msgTe = aiService.generateGreetingMessage(template, com.example.data.model.Language.TELUGU)
        val msgEn = aiService.generateGreetingMessage(template, com.example.data.model.Language.ENGLISH)

        assertTrue("Telugu message must not be blank", msgTe.isNotBlank())
        assertTrue("English message must not be blank", msgEn.isNotBlank())

        val wordsTe = msgTe.split("\\s+".toRegex()).size
        val wordsEn = msgEn.split("\\s+".toRegex()).size
        assertTrue("Telugu message must be <= 25 words (was $wordsTe)", wordsTe <= 25)
        assertTrue("English message must be <= 25 words (was $wordsEn)", wordsEn <= 25)
    }
}
