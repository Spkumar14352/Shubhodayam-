package com.example.data.invitation

import android.content.Context
import android.graphics.Color
import com.example.data.festivals.FestivalData
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

enum class Occasion(val key: String, val nameEn: String, val nameTe: String) {
    WEDDING("wedding", "Wedding", "వివాహం"),
    GRIHA_PRAVESAM("griha_pravesam", "Griha Pravesam", "గృహప్రవేశం"),
    UPANAYANAM("upanayanam", "Upanayanam", "ఉపనయనం"),
    NAMAKARANAM("namakaranam", "Namakaranam", "నామకరణం"),
    HALF_SAREE("half_saree", "Half-saree / Dhoti", "ఓణీల / పంచెల వేడుక"),
    BIRTHDAY("birthday", "Birthday", "పుట్టినరోజు");

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: WEDDING
    }
}

enum class InvitationLanguage { TELUGU, ENGLISH, BILINGUAL }

enum class PhotoFrame { CIRCLE, ARCH }

enum class InvitationPage { COVER, DETAILS, EVENTS }

data class InvitationEvent(val name: String, val date: LocalDate?, val time: LocalTime?)

/** A saved invitation draft. Free text is kept exactly as the user typed it. */
data class Invitation(
    val id: String = UUID.randomUUID().toString(),
    val occasion: Occasion = Occasion.WEDDING,
    val templateId: String = "kalyanam_classic",
    val language: InvitationLanguage = InvitationLanguage.BILINGUAL,
    /** Bride, or the child / family for other occasions. */
    val primaryName: String = "",
    val primaryParents: String = "",
    /** Groom (weddings only). */
    val secondaryName: String = "",
    val secondaryParents: String = "",
    /** For child functions: chooses "S/o" or "D/o". */
    val childIsBoy: Boolean = true,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val lagnam: String = "",
    val venueName: String = "",
    val venueAddress: String = "",
    val mapsLink: String = "",
    val events: List<InvitationEvent> = emptyList(),
    val compliments: String = "",
    val deityLine: String = DEFAULT_DEITY_LINE,
    val showDeityLine: Boolean = true,
    val invitationText: String = "",
    val photoPath: String = "",
    val photoFrame: PhotoFrame = PhotoFrame.CIRCLE,
    val photoScale: Float = 1f,
    val photoOffsetX: Float = 0f,
    val photoOffsetY: Float = 0f,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val pages: List<InvitationPage>
        get() = listOf(InvitationPage.COVER, InvitationPage.DETAILS) +
            if (events.any { it.name.isNotBlank() }) listOf(InvitationPage.EVENTS) else emptyList()

    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("occasion", occasion.key)
        .put("templateId", templateId)
        .put("language", language.name)
        .put("primaryName", primaryName)
        .put("primaryParents", primaryParents)
        .put("secondaryName", secondaryName)
        .put("secondaryParents", secondaryParents)
        .put("childIsBoy", childIsBoy)
        .put("date", date?.toString() ?: "")
        .put("time", time?.toString() ?: "")
        .put("lagnam", lagnam)
        .put("venueName", venueName)
        .put("venueAddress", venueAddress)
        .put("mapsLink", mapsLink)
        .put("events", JSONArray(events.map {
            JSONObject().put("name", it.name).put("date", it.date?.toString() ?: "").put("time", it.time?.toString() ?: "")
        }))
        .put("compliments", compliments)
        .put("deityLine", deityLine)
        .put("showDeityLine", showDeityLine)
        .put("invitationText", invitationText)
        .put("photoPath", photoPath)
        .put("photoFrame", photoFrame.name)
        .put("photoScale", photoScale.toDouble())
        .put("photoOffsetX", photoOffsetX.toDouble())
        .put("photoOffsetY", photoOffsetY.toDouble())
        .put("updatedAt", updatedAt)

    companion object {
        const val DEFAULT_DEITY_LINE = "శ్రీ వేంకటేశ్వర స్వామి కృప"
        const val MAX_EVENTS = 4

        fun fromJson(json: JSONObject): Invitation {
            fun date(value: String) = value.takeIf { it.isNotEmpty() }?.let(LocalDate::parse)
            fun time(value: String) = value.takeIf { it.isNotEmpty() }?.let(LocalTime::parse)
            val events = json.optJSONArray("events") ?: JSONArray()
            return Invitation(
                id = json.getString("id"),
                occasion = Occasion.fromKey(json.optString("occasion")),
                templateId = json.optString("templateId", "kalyanam_classic"),
                language = runCatching { InvitationLanguage.valueOf(json.optString("language")) }.getOrDefault(InvitationLanguage.BILINGUAL),
                primaryName = json.optString("primaryName"),
                primaryParents = json.optString("primaryParents"),
                secondaryName = json.optString("secondaryName"),
                secondaryParents = json.optString("secondaryParents"),
                childIsBoy = json.optBoolean("childIsBoy", true),
                date = date(json.optString("date")),
                time = time(json.optString("time")),
                lagnam = json.optString("lagnam"),
                venueName = json.optString("venueName"),
                venueAddress = json.optString("venueAddress"),
                mapsLink = json.optString("mapsLink"),
                events = (0 until events.length()).map { i ->
                    val e = events.getJSONObject(i)
                    InvitationEvent(e.optString("name"), date(e.optString("date")), time(e.optString("time")))
                },
                compliments = json.optString("compliments"),
                deityLine = json.optString("deityLine", DEFAULT_DEITY_LINE),
                showDeityLine = json.optBoolean("showDeityLine", true),
                invitationText = json.optString("invitationText"),
                photoPath = json.optString("photoPath"),
                photoFrame = runCatching { PhotoFrame.valueOf(json.optString("photoFrame")) }.getOrDefault(PhotoFrame.CIRCLE),
                photoScale = json.optDouble("photoScale", 1.0).toFloat(),
                photoOffsetX = json.optDouble("photoOffsetX", 0.0).toFloat(),
                photoOffsetY = json.optDouble("photoOffsetY", 0.0).toFloat(),
                updatedAt = json.optLong("updatedAt")
            )
        }
    }
}

enum class TemplateFont { SERIF, SANS, CONDENSED, CURSIVE }

data class InvitationTemplate(
    val id: String,
    val occasion: Occasion,
    val nameEn: String,
    val nameTe: String,
    val backgroundTop: Int,
    val backgroundBottom: Int,
    val border: String,
    val borderColor: Int,
    val motifs: List<String>,
    val headingFont: TemplateFont,
    val bodyFont: TemplateFont,
    val headingColor: Int,
    val textColor: Int,
    val accentColor: Int,
    val slots: Map<InvitationPage, List<String>>
) {
    companion object {
        const val FILE = "invitationTemplates.ts"
        private var cached: List<InvitationTemplate>? = null

        @Synchronized
        fun all(context: Context): List<InvitationTemplate> = cached ?: parse(
            context.assets.open("${FestivalData.ASSET_DIR}/$FILE").bufferedReader().use { it.readText() }
        ).also { cached = it }

        fun parse(typescript: String): List<InvitationTemplate> = FestivalData.objects(typescript).map { json ->
            fun strings(array: JSONArray?) = (0 until (array?.length() ?: 0)).map { array!!.getString(it) }
            fun font(key: String) = TemplateFont.valueOf(json.optString(key, "serif").uppercase())
            val background = json.getJSONArray("background")
            val slots = json.getJSONObject("slots")
            InvitationTemplate(
                id = json.getString("id"),
                occasion = Occasion.fromKey(json.getString("occasion")),
                nameEn = json.getString("nameEn"),
                nameTe = json.getString("nameTe"),
                backgroundTop = Color.parseColor(background.getString(0)),
                backgroundBottom = Color.parseColor(background.getString(1)),
                border = json.getString("border"),
                borderColor = Color.parseColor(json.getString("borderColor")),
                motifs = strings(json.optJSONArray("motifs")),
                headingFont = font("headingFont"),
                bodyFont = font("bodyFont"),
                headingColor = Color.parseColor(json.getString("headingColor")),
                textColor = Color.parseColor(json.getString("textColor")),
                accentColor = Color.parseColor(json.getString("accentColor")),
                slots = mapOf(
                    InvitationPage.COVER to strings(slots.optJSONArray("cover")),
                    InvitationPage.DETAILS to strings(slots.optJSONArray("details")),
                    InvitationPage.EVENTS to strings(slots.optJSONArray("events"))
                )
            )
        }
    }
}
