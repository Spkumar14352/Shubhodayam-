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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.ImageShareSheet
import com.example.data.PanchangNames
import com.example.data.model.AppConfig
import com.example.data.model.City
import com.example.data.model.Language
import com.example.data.muhurtham.MuhurthamCalendar
import com.example.data.muhurtham.MuhurthamDay
import com.example.data.muhurtham.MuhurthamType
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldDark
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.CalendarExport
import com.example.utils.InfoCardGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val DAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
private val TELUGU_WEEKDAYS = listOf("సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం", "ఆదివారం")
private val TELUGU_MONTHS = listOf(
    "జనవరి", "ఫిబ్రవరి", "మార్చి", "ఏప్రిల్", "మే", "జూన్", "జూలై", "ఆగస్టు", "సెప్టెంబర్", "అక్టోబర్", "నవంబర్", "డిసెంబర్"
)

@Composable
fun MuhurthamScreen(
    language: Language,
    city: City,
    onCardShared: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val isTelugu = language == Language.TELUGU
    var selectedType by remember { mutableStateOf(MuhurthamType.MARRIAGE) }
    var sharing by remember { mutableStateOf<MuhurthamDay?>(null) }

    val allDays by produceState<Map<MuhurthamType, List<MuhurthamDay>>?>(null, city) {
        value = withContext(Dispatchers.Default) {
            MuhurthamCalendar.get(context, LocalDate.now(), city.latitude, city.longitude)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("muhurtham_screen")
    ) {
        SubScreenHeader(title = Translations.tr("menu_muhurthams", language), onBack = onBack)

        Surface(color = TempleSaffron.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isTelugu) {
                    "సాధారణ మార్గదర్శకం మాత్రమే. ఖచ్చితమైన ముహూర్తాన్ని మీ కుటుంబ పురోహితులతో నిర్ధారించుకోండి."
                } else {
                    "General guidance only. Please confirm the exact muhurtham with your family purohit."
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("muhurtham_disclaimer")
            )
        }

        ScrollableTabRow(
            selectedTabIndex = selectedType.ordinal,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MuhurthamType.entries.forEach { type ->
                Tab(
                    selected = type == selectedType,
                    onClick = { selectedType = type },
                    text = { Text(if (isTelugu) type.nameTe else type.key) },
                    modifier = Modifier.testTag("muhurtham_tab_${type.name}")
                )
            }
        }

        val days = allDays
        if (days == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TempleGold)
            }
            return@Column
        }

        val forType = days[selectedType].orEmpty()
        val grouped = forType.groupBy {
            if (isTelugu) "${TELUGU_MONTHS[it.date.monthValue - 1]} ${it.date.year}"
            else "${it.date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${it.date.year}"
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (forType.isEmpty()) {
                item {
                    Text(
                        text = if (isTelugu) {
                            "${selectedType.nameTe} కోసం సూచించిన రోజులు లేవు. దయచేసి మీ పురోహితులను సంప్రదించండి."
                        } else {
                            "No suggested days for ${selectedType.key}. Please consult your purohit."
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            grouped.forEach { (month, monthDays) ->
                item {
                    Text(
                        text = month,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TempleMaroon,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(monthDays, key = { "${it.type}_${it.date}" }) { day ->
                    MuhurthamCard(
                        language = language,
                        day = day,
                        onSaveToCalendar = {
                            val lines = dayLines(language, day)
                            CalendarExport.open(
                                context = context,
                                title = "${if (isTelugu) day.type.nameTe else day.type.key} – ${if (isTelugu) "ముహూర్తం" else "Muhurtham"}",
                                date = day.date,
                                start = day.windowStart,
                                end = day.windowEnd,
                                description = (lines.map { "${it.first}: ${it.second}" } + disclaimer(isTelugu)).joinToString("\n")
                            )
                        },
                        onShare = { sharing = day }
                    )
                }
            }
        }
    }

    sharing?.let { day ->
        val title = "${if (isTelugu) day.type.nameTe else day.type.key} ${if (isTelugu) "ముహూర్తం" else "Muhurtham"}"
        ImageShareSheet(
            language = language,
            title = title,
            shareText = "$title – ${day.date.format(DAY_FORMAT)} – Shubhodaya app: ${AppConfig.PLAY_STORE_URL}",
            onShared = onCardShared,
            onDismiss = { sharing = null },
            createBitmap = {
                InfoCardGenerator.createCard(
                    title = title,
                    subtitle = "${day.date.format(DAY_FORMAT)} • ${if (isTelugu) city.nameTe else city.nameEn}",
                    rows = dayLines(language, day),
                    notes = listOf(badgeText(isTelugu, day.isVerified), disclaimer(isTelugu))
                )
            }
        )
    }
}

private fun disclaimer(isTelugu: Boolean) = if (isTelugu) {
    "సాధారణ మార్గదర్శకం మాత్రమే. ఖచ్చితమైన ముహూర్తాన్ని మీ కుటుంబ పురోహితులతో నిర్ధారించుకోండి."
} else {
    "General guidance only. Please confirm the exact muhurtham with your family purohit."
}

private fun badgeText(isTelugu: Boolean, verified: Boolean) = when {
    verified && isTelugu -> "ధృవీకరించబడింది"
    verified -> "Verified"
    isTelugu -> "సాధారణ మార్గదర్శకం"
    else -> "General guidance"
}

private fun dayLines(language: Language, day: MuhurthamDay): List<Pair<String, String>> {
    val isTelugu = language == Language.TELUGU
    fun t(te: String, en: String) = if (isTelugu) te else en
    val tithi = PanchangNames.getTithi(day.tithi)
    val nakshatra = PanchangNames.getNakshatra(day.nakshatra)
    val window = if (day.windowStart != null && day.windowEnd != null) {
        val range = "${day.windowStart.format(TIME_FORMAT)} – ${day.windowEnd.format(TIME_FORMAT)}"
        when {
            day.isAbhijit -> "$range (${t("అభిజిత్", "Abhijit")})"
            day.isVerified -> range
            else -> "$range (${t("రాహుకాలం, యమగండం కాకుండా", "avoids Rahu Kalam & Yamagandam")})"
        }
    } else {
        t("పురోహితులను అడగండి", "Ask your purohit")
    }
    return listOfNotNull(
        t("వారం", "Vaaram") to if (isTelugu) TELUGU_WEEKDAYS[day.date.dayOfWeek.value - 1] else day.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
        t("తిథి", "Tithi") to if (isTelugu) tithi.nameTe else tithi.nameEn,
        t("నక్షత్రం", "Nakshatra") to if (isTelugu) nakshatra.nameTe else nakshatra.nameEn,
        t("మంచి సమయం", "Good time") to window,
        day.lagnam.takeIf { it.isNotBlank() }?.let { t("లగ్నం", "Lagnam") to it },
        day.notes.takeIf { it.isNotBlank() }?.let { t("గమనిక", "Notes") to it }
    )
}

@Composable
private fun MuhurthamCard(
    language: Language,
    day: MuhurthamDay,
    onSaveToCalendar: () -> Unit,
    onShare: () -> Unit
) {
    val isTelugu = language == Language.TELUGU

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("muhurtham_day_${day.type.name}_${day.date}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(if (day.isVerified) 1.5.dp else 0.5.dp, if (day.isVerified) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = day.date.format(DAY_FORMAT),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (day.isVerified) TempleGold else Color(0xFF757575).copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = badgeText(isTelugu, day.isVerified),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isVerified) Color(0xFF380712) else Color(0xFF616161),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            dayLines(language, day).forEach { (label, value) ->
                Row(modifier = Modifier.padding(end = 8.dp, top = 2.dp)) {
                    Text(text = label, fontSize = 12.sp, color = TempleGoldDark, modifier = Modifier.width(96.dp))
                    Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Row {
                TextButton(onClick = onSaveToCalendar, modifier = Modifier.testTag("muhurtham_calendar_${day.date}")) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = TempleMaroon)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTelugu) "క్యాలెండర్‌లో సేవ్" else "Save to calendar", color = TempleMaroon)
                }
                TextButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = TempleMaroon)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTelugu) "షేర్" else "Share", color = TempleMaroon)
                }
            }
        }
    }
}
