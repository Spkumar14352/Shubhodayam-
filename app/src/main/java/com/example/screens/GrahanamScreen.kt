package com.example.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.ImageShareSheet
import com.example.data.eclipses.Eclipse
import com.example.data.eclipses.EclipseBody
import com.example.data.model.AppConfig
import com.example.data.model.City
import com.example.data.model.Language
import com.example.i18n.Translations
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.InfoCardGenerator
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
private val DATE = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)

@Composable
fun GrahanamScreen(
    language: Language,
    city: City,
    eclipses: List<Eclipse>?,
    onCardShared: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val isTelugu = language == Language.TELUGU
    fun t(te: String, en: String) = if (isTelugu) te else en
    var sharing by remember { mutableStateOf<Eclipse?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("grahanam_screen")
    ) {
        SubScreenHeader(title = Translations.tr("menu_grahanam", language), onBack = onBack)

        if (eclipses == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TempleGold)
            }
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = t(
                        "రాబోయే ${com.example.data.eclipses.EclipseConstants.LOOKAHEAD_MONTHS} నెలల గ్రహణాలు – ${city.nameTe} సమయాలు (IST)",
                        "Eclipses in the next ${com.example.data.eclipses.EclipseConstants.LOOKAHEAD_MONTHS} months – timings for ${city.nameEn} (IST)"
                    ),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (eclipses.isEmpty()) {
                item {
                    Text(
                        text = t("ఈ కాలంలో గ్రహణాలు లేవు.", "No eclipses in this period."),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(eclipses, key = { it.id }) { eclipse ->
                EclipseCard(language, city, eclipse, onShare = { sharing = eclipse })
            }

            item { TraditionalCustomsCard(language) }
        }
    }

    sharing?.let { eclipse ->
        val title = if (isTelugu) eclipse.nameTe else eclipse.nameEn
        ImageShareSheet(
            language = language,
            title = title,
            shareText = "$title – ${eclipse.start.format(DATE)} – Shubhodaya app: ${AppConfig.PLAY_STORE_URL}",
            onShared = onCardShared,
            onDismiss = { sharing = null },
            createBitmap = {
                InfoCardGenerator.createCard(
                    title = title,
                    subtitle = "${eclipse.start.format(DATE)} • ${if (isTelugu) city.nameTe else city.nameEn} (IST)",
                    rows = timingRows(language, eclipse),
                    notes = eclipseNotes(language, city, eclipse)
                )
            }
        )
    }
}

private fun timingRows(language: Language, eclipse: Eclipse): List<Pair<String, String>> {
    val isTelugu = language == Language.TELUGU
    fun t(te: String, en: String) = if (isTelugu) te else en
    return listOfNotNull(
        t("స్పర్శ (ప్రారంభం)", "Sparsha (start)") to eclipse.start.format(DATE_TIME),
        t("మధ్య (గరిష్ఠం)", "Madhya (maximum)") to eclipse.peak.format(DATE_TIME),
        t("మోక్షం (ముగింపు)", "Moksha (end)") to eclipse.end.format(DATE_TIME),
        t("వ్యవధి", "Duration") to formatDuration(eclipse.duration, isTelugu),
        eclipse.sutakStart?.let { t("సూతకం (వేధ) ప్రారంభం – సంప్రదాయం ప్రకారం", "Sutak (vedha) begins – As per tradition") to it.format(DATE_TIME) }
    )
}

private fun eclipseNotes(language: Language, city: City, eclipse: Eclipse): List<String> {
    val isTelugu = language == Language.TELUGU
    fun t(te: String, en: String) = if (isTelugu) te else en
    return listOfNotNull(
        if (eclipse.isVisible) {
            t("${city.nameTe} నుండి కనిపిస్తుంది.", "Visible from ${city.nameEn}.")
        } else {
            t(
                "${city.nameTe} నుండి కనిపించదు. సాధారణంగా కనిపించే గ్రహణాలకే సూతకం పాటిస్తారు.",
                "Not visible from ${city.nameEn}. Sutak is usually observed only for eclipses visible locally."
            )
        },
        if (!eclipse.isTraditionallyObserved) {
            t("ఉపచ్ఛాయ గ్రహణం – సంప్రదాయంగా సాధారణంగా పాటించరు.", "Penumbral eclipse – not usually observed traditionally.")
        } else null,
        if (eclipse.body == EclipseBody.SOLAR) {
            t("సూర్యుని నేరుగా ఎప్పుడూ చూడవద్దు – ధృవీకరించిన గ్రహణ కళ్లద్దాలు వాడండి.", "Never look at the Sun directly – use certified eclipse glasses.")
        } else null
    )
}

private fun formatDuration(duration: Duration, isTelugu: Boolean): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    return if (isTelugu) "$hours గం $minutes ని" else "$hours h $minutes min"
}

@Composable
private fun EclipseCard(language: Language, city: City, eclipse: Eclipse, onShare: () -> Unit) {
    val isTelugu = language == Language.TELUGU

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("eclipse_card_${eclipse.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTelugu) eclipse.nameTe else eclipse.nameEn,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = eclipse.start.format(DATE), fontSize = 13.sp, color = TempleSaffron)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = (if (eclipse.isVisible) AuspiciousGreen else Color(0xFF757575)).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = when {
                            eclipse.isVisible && isTelugu -> "కనిపిస్తుంది"
                            eclipse.isVisible -> "Visible"
                            isTelugu -> "కనిపించదు"
                            else -> "Not visible"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (eclipse.isVisible) AuspiciousGreen else Color(0xFF757575),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            timingRows(language, eclipse).forEach { (label, value) ->
                Row {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            eclipseNotes(language, city, eclipse).forEach { note ->
                val isSafety = note.contains("glasses") || note.contains("కళ్లద్దాలు")
                Text(
                    text = note,
                    fontSize = 12.sp,
                    fontWeight = if (isSafety) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSafety) InauspiciousRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onShare,
                border = BorderStroke(1.dp, TempleGold),
                modifier = Modifier.testTag("eclipse_share_${eclipse.id}")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = TempleGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Translations.tr("share_on_whatsapp", language), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun TraditionalCustomsCard(language: Language) {
    val isTelugu = language == Language.TELUGU
    val customs = if (isTelugu) {
        listOf(
            "చాలా కుటుంబాలు ఈ సమయంలో వంట చేయడం, భోజనం చేయడం మానుకుంటాయి.",
            "గ్రహణ సమయంలో దేవాలయాలు సాధారణంగా మూసివేస్తారు; శుద్ధి తర్వాత తిరిగి తెరుస్తారు.",
            "మోక్షం తర్వాత స్నానం చేయడం సాధారణ ఆచారం.",
            "చాలా మంది గ్రహణ సమయంలో జపం, ధ్యానం చేస్తారు."
        )
    } else {
        listOf(
            "Many families avoid cooking and eating during this period.",
            "Temples usually close during grahanam and reopen after cleansing.",
            "Bathing after moksha is a common custom.",
            "Many people chant or meditate during the eclipse."
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grahanam_customs"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TempleMaroon.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (isTelugu) "సంప్రదాయ ఆచారాలు" else "Traditional customs",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            customs.forEach { Text(text = "• $it", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isTelugu) {
                    "సూర్య గ్రహణాన్ని నేరుగా ఎప్పుడూ చూడవద్దు – ధృవీకరించిన గ్రహణ కళ్లద్దాలు వాడండి."
                } else {
                    "Never look at the Sun directly – use certified eclipse glasses."
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = InauspiciousRed
            )
        }
    }
}

/** The next eclipse starting within [days] days of [now], or null. */
fun upcomingEclipseWithin(eclipses: List<Eclipse>, now: LocalDateTime, days: Long): Eclipse? =
    eclipses.firstOrNull { it.end > now && !it.start.toLocalDate().isAfter(now.toLocalDate().plusDays(days)) }
