package com.example

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.invitation.Invitation
import com.example.data.invitation.InvitationEvent
import com.example.data.invitation.InvitationLanguage
import com.example.data.invitation.InvitationPage
import com.example.data.invitation.InvitationStore
import com.example.data.invitation.InvitationTemplate
import com.example.data.invitation.Occasion
import com.example.data.invitation.PhotoFrame
import com.example.services.InvitationAiService
import com.example.utils.InvitationRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ShubhalekhaTest {
    private val templates = InvitationTemplate.parse(
        listOf(File("../data"), File("data")).first { File(it, InvitationTemplate.FILE).exists() }
            .resolve(InvitationTemplate.FILE).readText()
    )

    private val sample = Invitation(
        occasion = Occasion.WEDDING,
        language = InvitationLanguage.BILINGUAL,
        primaryName = "Sita",
        primaryParents = "Smt. & Sri Ramaiah",
        secondaryName = "Rama",
        secondaryParents = "Smt. & Sri Dasharatha",
        date = LocalDate.of(2026, 12, 3),
        time = LocalTime.of(10, 24),
        lagnam = "Makara",
        venueName = "Sri Kalyana Mandapam",
        venueAddress = "Main Road, Vijayawada",
        mapsLink = "https://maps.google.com/?q=Vijayawada",
        events = listOf(InvitationEvent("Sangeet", LocalDate.of(2026, 12, 2), LocalTime.of(18, 0))),
        compliments = "Friends and family"
    )

    @Test
    fun templatesCoverEveryOccasion() {
        assertTrue(templates.count { it.occasion == Occasion.WEDDING } >= 10)
        Occasion.entries.forEach { occasion -> assertTrue(occasion.name, templates.any { it.occasion == occasion }) }
        assertEquals(templates.size, templates.map { it.id }.toSet().size)
    }

    @Test
    fun draftsRoundTripThroughJson() {
        val copy = Invitation.fromJson(sample.copy(photoFrame = PhotoFrame.ARCH, photoScale = 1.6f).toJson())
        assertEquals(sample.copy(photoFrame = PhotoFrame.ARCH, photoScale = 1.6f), copy)
    }

    @Test
    fun parentLinesUseSonAndDaughterText() {
        val lines = InvitationRenderer.parentLines(sample, InvitationLanguage.ENGLISH)
        assertEquals("Sita\nD/o Smt. & Sri Ramaiah", lines[0])
        assertEquals("Rama\nS/o Smt. & Sri Dasharatha", lines[1])
        assertTrue(InvitationRenderer.parentLines(sample, InvitationLanguage.TELUGU)[0].endsWith("గారి కుమార్తె"))
    }

    @Test
    fun eventsPageOnlyWhenThereAreEvents() {
        assertEquals(3, sample.pages.size)
        assertEquals(listOf(InvitationPage.COVER, InvitationPage.DETAILS), sample.copy(events = emptyList()).pages)
    }

    @Test
    fun aiTextIsCutToSixtyWords() {
        val long = (1..80).joinToString(" ") { "word$it" }
        assertEquals(60, InvitationAiService.limitWords("\"$long\"", 60).split(" ").size)
    }

    @Test
    fun unlockIsRememberedPerInvitation() {
        val store = InvitationStore(RuntimeEnvironment.getApplication())
        assertFalse(store.isUnlocked(sample.id))
        store.unlockInvitation(sample.id)
        assertTrue(store.isUnlocked(sample.id))
        assertFalse(store.isUnlocked("another"))
    }

    @Test
    fun everyTemplateRendersEveryPage() {
        val photo = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GRAY) }
        val out = File("build/reports/shubhalekha").apply { mkdirs() }
        templates.forEach { template ->
            val invitation = sample.copy(occasion = template.occasion, templateId = template.id, photoFrame = PhotoFrame.ARCH)
            invitation.pages.forEach { page ->
                val bitmap = InvitationRenderer.render(page, invitation, template, 540, 960, watermark = true, photo = photo)
                assertEquals(540, bitmap.width)
                File(out, "${template.id}_${page.name.lowercase()}.png").outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        }
    }
}
