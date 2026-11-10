@file:OptIn(ExperimentalLayoutApi::class)

package com.example.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.invitation.Invitation
import com.example.data.invitation.InvitationEvent
import com.example.data.invitation.InvitationLanguage
import com.example.data.invitation.InvitationPage
import com.example.data.invitation.InvitationStore
import com.example.data.invitation.InvitationTemplate
import com.example.data.invitation.Occasion
import com.example.data.invitation.PhotoFrame
import com.example.data.model.City
import com.example.data.model.Language
import com.example.data.muhurtham.MuhurthamCalendar
import com.example.data.muhurtham.MuhurthamType
import com.example.i18n.Translations
import com.example.services.InvitationAiResult
import com.example.services.InvitationAiService
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.InvitationExport
import com.example.utils.InvitationRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SHORT_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val SHORT_TIME = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

/** Label in both languages, as the form shows Telugu and English together. */
private fun bi(te: String, en: String) = "$te / $en"

@Composable
fun ShubhalekhaScreen(
    language: Language,
    city: City,
    onCardShared: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val store = remember { InvitationStore(context) }
    val templates = remember { InvitationTemplate.all(context) }
    var drafts by remember { mutableStateOf(store.list()) }
    var editing by remember { mutableStateOf<Invitation?>(null) }

    val leave = {
        if (editing != null) {
            editing?.let(store::save)
            editing = null
            drafts = store.list()
        } else {
            onBack()
        }
    }
    BackHandler(onBack = leave)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("shubhalekha_screen")
    ) {
        SubScreenHeader(title = Translations.tr("menu_shubhalekha", language), onBack = leave)

        val current = editing
        if (current == null) {
            DraftList(
                language = language,
                drafts = drafts,
                onNew = { editing = Invitation() },
                onEdit = { editing = it },
                onDelete = {
                    store.delete(it)
                    drafts = store.list()
                }
            )
        } else {
            // Save a moment after the user stops typing.
            LaunchedEffect(current) {
                delay(600)
                withContext(Dispatchers.IO) { store.save(current) }
            }
            InvitationEditor(
                language = language,
                city = city,
                invitation = current,
                templates = templates,
                store = store,
                onChange = { editing = it },
                onShared = onCardShared
            )
        }
    }
}

@Composable
private fun DraftList(
    language: Language,
    drafts: List<Invitation>,
    onNew: () -> Unit,
    onEdit: (Invitation) -> Unit,
    onDelete: (Invitation) -> Unit
) {
    val isTelugu = language == Language.TELUGU
    var confirmDelete by remember { mutableStateOf<Invitation?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                onClick = onNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("shubhalekha_new"),
                colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTelugu) "కొత్త శుభలేఖ" else "New invitation", fontWeight = FontWeight.Bold)
            }
        }
        item {
            Text(
                text = if (isTelugu) "నా శుభలేఖలు" else "My invitations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (drafts.isEmpty()) {
            item {
                Text(
                    text = if (isTelugu) "ఇంకా శుభలేఖలు లేవు." else "No invitations yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
        items(drafts, key = { it.id }) { draft ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEdit(draft) }
                    .testTag("shubhalekha_draft_${draft.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.5.dp, TempleGold.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        val names = listOf(draft.primaryName, draft.secondaryName).filter { it.isNotBlank() }.joinToString(" & ")
                        Text(
                            text = names.ifBlank { if (isTelugu) "పేరు లేని శుభలేఖ" else "Untitled invitation" },
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${if (isTelugu) draft.occasion.nameTe else draft.occasion.nameEn}${draft.date?.let { " • ${it.format(SHORT_DATE)}" } ?: ""}",
                            fontSize = 12.sp,
                            color = TempleSaffron
                        )
                    }
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TempleGold)
                    IconButton(onClick = { confirmDelete = draft }) {
                        Icon(Icons.Default.Delete, contentDescription = if (isTelugu) "తొలగించు" else "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    confirmDelete?.let { draft ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            text = { Text(if (isTelugu) "ఈ శుభలేఖను తొలగించాలా?" else "Delete this invitation?") },
            confirmButton = {
                TextButton(onClick = { onDelete(draft); confirmDelete = null }) { Text(if (isTelugu) "తొలగించు" else "Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = null }) { Text(Translations.tr("close", language)) }
            }
        )
    }
}

private val STEP_TITLES = listOf(
    "డిజైన్" to "Design",
    "పేర్లు" to "Names",
    "ముహూర్తం" to "Muhurtham",
    "వేదిక" to "Venue",
    "కార్యక్రమాలు" to "Events",
    "అదనపు" to "Extras",
    "ఎగుమతి" to "Export"
)

@Composable
private fun InvitationEditor(
    language: Language,
    city: City,
    invitation: Invitation,
    templates: List<InvitationTemplate>,
    store: InvitationStore,
    onChange: (Invitation) -> Unit,
    onShared: () -> Unit
) {
    val isTelugu = language == Language.TELUGU
    var step by remember { mutableStateOf(0) }
    var selectedPage by remember { mutableStateOf(InvitationPage.COVER) }
    var unlocked by remember(invitation.id) { mutableStateOf(store.isUnlocked(invitation.id)) }
    val template = templates.firstOrNull { it.id == invitation.templateId } ?: templates.first()
    val pages = invitation.pages
    // The Events page disappears when the last event is removed.
    val page = if (selectedPage in pages) selectedPage else InvitationPage.COVER

    val photo by produceState<Bitmap?>(null, invitation.photoPath) {
        value = withContext(Dispatchers.IO) { InvitationExport.loadPhoto(invitation) }
    }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(invitation, template, page, photo, unlocked) {
        delay(200)
        preview = withContext(Dispatchers.Default) {
            InvitationRenderer.render(page, invitation, template, 540, 960, watermark = !unlocked, photo = photo)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Live preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(230.dp)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .testTag("shubhalekha_preview"),
                contentAlignment = Alignment.Center
            ) {
                preview?.let { Image(it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize()) }
                    ?: CircularProgressIndicator(color = TempleGold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pages.forEach { p ->
                    val label = when (p) {
                        InvitationPage.COVER -> if (isTelugu) "ముఖచిత్రం" else "Cover"
                        InvitationPage.DETAILS -> if (isTelugu) "వివరాలు" else "Details"
                        InvitationPage.EVENTS -> if (isTelugu) "కార్యక్రమాలు" else "Events"
                    }
                    Chip(label, p == page) { selectedPage = p }
                }
            }
        }

        // Step selector
        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            STEP_TITLES.forEachIndexed { index, (te, en) ->
                Chip("${index + 1}. ${if (isTelugu) te else en}", index == step, Modifier.testTag("shubhalekha_step_$index")) { step = index }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (step) {
                0 -> DesignStep(isTelugu, invitation, templates, onChange)
                1 -> NamesStep(invitation, onChange)
                2 -> MuhurthamStep(isTelugu, city, invitation, onChange)
                3 -> VenueStep(invitation, onChange)
                4 -> EventsStep(invitation, onChange)
                5 -> ExtrasStep(isTelugu, invitation, photo, store, onChange)
                else -> ExportStep(isTelugu, invitation, template, unlocked, store, onUnlocked = { unlocked = true }, onShared = onShared)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (step > 0) {
                    OutlinedButton(onClick = { step-- }, modifier = Modifier.weight(1f)) { Text(if (isTelugu) "వెనుకకు" else "Back") }
                }
                if (step < STEP_TITLES.lastIndex) {
                    Button(
                        onClick = { step++ },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shubhalekha_next"),
                        colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
                    ) { Text(if (isTelugu) "తర్వాత" else "Next") }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TempleMaroon, selectedLabelColor = Color.White)
    )
}

@Composable
private fun Field(label: String, value: String, modifier: Modifier = Modifier, singleLine: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text, onValue: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun DesignStep(isTelugu: Boolean, invitation: Invitation, templates: List<InvitationTemplate>, onChange: (Invitation) -> Unit) {
    SectionTitle(bi("సందర్భం", "Occasion"))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Occasion.entries.forEach { occasion ->
            Chip(if (isTelugu) occasion.nameTe else occasion.nameEn, occasion == invitation.occasion) {
                val first = templates.firstOrNull { it.occasion == occasion } ?: templates.first()
                onChange(invitation.copy(occasion = occasion, templateId = first.id))
            }
        }
    }

    SectionTitle(bi("డిజైన్", "Design"))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val forOccasion = templates.filter { it.occasion == invitation.occasion }.ifEmpty { templates }
        forOccasion.forEach { template ->
            val selected = template.id == invitation.templateId
            Column(
                modifier = Modifier
                    .width(96.dp)
                    .clickable { onChange(invitation.copy(templateId = template.id)) }
                    .testTag("template_${template.id}"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(Color(template.backgroundTop), Color(template.backgroundBottom))))
                        .border(if (selected) 3.dp else 2.dp, Color(template.borderColor), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aa", color = Color(template.headingColor), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Text(
                    text = if (isTelugu) template.nameTe else template.nameEn,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    SectionTitle(bi("భాష", "Language"))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(
            InvitationLanguage.TELUGU to "తెలుగు",
            InvitationLanguage.ENGLISH to "English",
            InvitationLanguage.BILINGUAL to "తెలుగు + English"
        ).forEach { (lang, label) ->
            Chip(label, invitation.language == lang) { onChange(invitation.copy(language = lang)) }
        }
    }
}

@Composable
private fun NamesStep(invitation: Invitation, onChange: (Invitation) -> Unit) {
    if (invitation.occasion == Occasion.WEDDING) {
        Field(bi("వధువు పేరు", "Bride name"), invitation.primaryName) { onChange(invitation.copy(primaryName = it)) }
        Field(bi("వధువు తల్లిదండ్రులు", "Bride's parents"), invitation.primaryParents) { onChange(invitation.copy(primaryParents = it)) }
        Field(bi("వరుడి పేరు", "Groom name"), invitation.secondaryName) { onChange(invitation.copy(secondaryName = it)) }
        Field(bi("వరుడి తల్లిదండ్రులు", "Groom's parents"), invitation.secondaryParents) { onChange(invitation.copy(secondaryParents = it)) }
    } else {
        val nameLabel = if (invitation.occasion == Occasion.GRIHA_PRAVESAM) bi("కుటుంబం పేరు", "Family name") else bi("చిన్నారి పేరు", "Child's name")
        Field(nameLabel, invitation.primaryName) { onChange(invitation.copy(primaryName = it)) }
        if (invitation.occasion != Occasion.GRIHA_PRAVESAM) {
            Field(bi("తల్లిదండ్రులు", "Parents"), invitation.primaryParents) { onChange(invitation.copy(primaryParents = it)) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Chip(bi("అబ్బాయి", "Boy"), invitation.childIsBoy) { onChange(invitation.copy(childIsBoy = true)) }
                Chip(bi("అమ్మాయి", "Girl"), !invitation.childIsBoy) { onChange(invitation.copy(childIsBoy = false)) }
            }
        }
    }
    // Shows exactly the "S/o" / "D/o" lines the card will print.
    InvitationRenderer.parentLines(invitation, invitation.language).forEach {
        Text(text = it, fontSize = 12.sp, color = TempleSaffron)
    }
}

@Composable
private fun PickerField(label: String, value: String?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value ?: "—", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private fun showDatePicker(context: android.content.Context, initial: LocalDate?, onPick: (LocalDate) -> Unit) {
    val start = initial ?: LocalDate.now()
    DatePickerDialog(context, { _, y, m, d -> onPick(LocalDate.of(y, m + 1, d)) }, start.year, start.monthValue - 1, start.dayOfMonth).show()
}

private fun showTimePicker(context: android.content.Context, initial: LocalTime?, onPick: (LocalTime) -> Unit) {
    val start = initial ?: LocalTime.of(10, 0)
    TimePickerDialog(context, { _, h, m -> onPick(LocalTime.of(h, m)) }, start.hour, start.minute, false).show()
}

@Composable
private fun MuhurthamStep(isTelugu: Boolean, city: City, invitation: Invitation, onChange: (Invitation) -> Unit) {
    val context = LocalContext.current
    var picking by remember { mutableStateOf(false) }
    val muhurthamType = when (invitation.occasion) {
        Occasion.WEDDING -> MuhurthamType.MARRIAGE
        Occasion.GRIHA_PRAVESAM -> MuhurthamType.GRIHA_PRAVESAM
        Occasion.UPANAYANAM -> MuhurthamType.UPANAYANAM
        Occasion.NAMAKARANAM -> MuhurthamType.NAMING
        else -> null
    }

    PickerField(bi("తేదీ", "Date"), invitation.date?.format(SHORT_DATE)) {
        showDatePicker(context, invitation.date) { onChange(invitation.copy(date = it)) }
    }
    PickerField(bi("సమయం", "Time"), invitation.time?.format(SHORT_TIME)) {
        showTimePicker(context, invitation.time) { onChange(invitation.copy(time = it)) }
    }
    if (muhurthamType != null) {
        OutlinedButton(
            onClick = { picking = true },
            border = BorderStroke(1.dp, TempleGold),
            modifier = Modifier.testTag("pick_from_muhurthams")
        ) { Text(bi("ముహూర్తాల నుండి ఎంచుకోండి", "Pick from Muhurthams")) }
    }
    Field(bi("లగ్నం (ఐచ్ఛికం)", "Lagnam (optional)"), invitation.lagnam) { onChange(invitation.copy(lagnam = it)) }

    if (picking && muhurthamType != null) {
        val days by produceState<List<com.example.data.muhurtham.MuhurthamDay>?>(null) {
            value = withContext(Dispatchers.Default) {
                MuhurthamCalendar.get(context, LocalDate.now(), city.latitude, city.longitude)[muhurthamType].orEmpty()
            }
        }
        AlertDialog(
            onDismissRequest = { picking = false },
            title = { Text(bi("ముహూర్తాలు", "Muhurthams")) },
            text = {
                val list = days
                if (list == null) {
                    CircularProgressIndicator(color = TempleGold)
                } else if (list.isEmpty()) {
                    Text(if (isTelugu) "సూచించిన రోజులు లేవు." else "No suggested days.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                        items(list) { day ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onChange(invitation.copy(date = day.date, time = day.windowStart ?: invitation.time, lagnam = day.lagnam.ifBlank { invitation.lagnam }))
                                        picking = false
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(day.date.format(SHORT_DATE), fontWeight = FontWeight.Bold)
                                Text(
                                    text = listOfNotNull(
                                        day.windowStart?.let { s -> day.windowEnd?.let { e -> "${s.format(SHORT_TIME)} – ${e.format(SHORT_TIME)}" } },
                                        if (day.isVerified) (if (isTelugu) "ధృవీకరించబడింది" else "Verified") else (if (isTelugu) "సాధారణ మార్గదర్శకం" else "General guidance")
                                    ).joinToString(" • "),
                                    fontSize = 12.sp,
                                    color = TempleSaffron
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { picking = false }) { Text(if (isTelugu) "మూసివేయి" else "Close") } }
        )
    }
}

@Composable
private fun VenueStep(invitation: Invitation, onChange: (Invitation) -> Unit) {
    Field(bi("వేదిక పేరు", "Venue name"), invitation.venueName) { onChange(invitation.copy(venueName = it)) }
    Field(bi("చిరునామా", "Address"), invitation.venueAddress, singleLine = false) { onChange(invitation.copy(venueAddress = it)) }
    Field(bi("Google Maps లింక్", "Google Maps link"), invitation.mapsLink, keyboardType = KeyboardType.Uri) {
        onChange(invitation.copy(mapsLink = it.trim()))
    }
    Text(
        text = bi("లింక్ కార్డుపై QR కోడ్‌గా కనిపిస్తుంది", "The link appears on the card as a QR code"),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private val EVENT_PRESETS = listOf("పెళ్లికూతురు (Pellikuthuru)", "మెహందీ (Mehendi)", "సంగీత్ (Sangeet)", "హల్దీ (Haldi)", "రిసెప్షన్ (Reception)")

@Composable
private fun EventsStep(invitation: Invitation, onChange: (Invitation) -> Unit) {
    val context = LocalContext.current
    fun update(index: Int, event: InvitationEvent) =
        onChange(invitation.copy(events = invitation.events.toMutableList().also { it[index] = event }))

    SectionTitle(bi("ఇతర కార్యక్రమాలు (గరిష్టం 4)", "Other events (up to 4)"))
    invitation.events.forEachIndexed { index, event ->
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(0.5.dp, TempleGold.copy(alpha = 0.5f))) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Field(bi("కార్యక్రమం", "Event"), event.name, modifier = Modifier.weight(1f)) { update(index, event.copy(name = it)) }
                    IconButton(onClick = { onChange(invitation.copy(events = invitation.events.filterIndexed { i, _ -> i != index })) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PickerField(bi("తేదీ", "Date"), event.date?.format(SHORT_DATE)) { showDatePicker(context, event.date ?: invitation.date) { update(index, event.copy(date = it)) } }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PickerField(bi("సమయం", "Time"), event.time?.format(SHORT_TIME)) { showTimePicker(context, event.time) { update(index, event.copy(time = it)) } }
                    }
                }
            }
        }
    }
    if (invitation.events.size < Invitation.MAX_EVENTS) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EVENT_PRESETS.forEach { preset ->
                Chip("+ $preset", false) { onChange(invitation.copy(events = invitation.events + InvitationEvent(preset, null, null))) }
            }
            Chip(bi("+ ఇతర", "+ Other"), false) { onChange(invitation.copy(events = invitation.events + InvitationEvent("", null, null))) }
        }
    }

    SectionTitle(bi("శుభాభినందనలతో", "With best compliments from"))
    Field(bi("పేర్లు", "Names"), invitation.compliments, singleLine = false) { onChange(invitation.copy(compliments = it)) }
}

private val ArchShape: Shape = GenericShape { size, _ ->
    val radius = size.width / 2f
    moveTo(0f, size.height)
    lineTo(0f, radius)
    arcTo(Rect(0f, 0f, size.width, size.width), 180f, 180f, false)
    lineTo(size.width, size.height)
    close()
}

@Composable
private fun ExtrasStep(isTelugu: Boolean, invitation: Invitation, photo: Bitmap?, store: InvitationStore, onChange: (Invitation) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var aiBusy by remember { mutableStateOf(false) }
    var aiMessage by remember { mutableStateOf<String?>(null) }
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val path = withContext(Dispatchers.IO) { runCatching { store.importPhoto(invitation.id, uri) }.getOrNull() }
                if (path != null) {
                    onChange(invitation.copy(photoPath = path, photoScale = 1f, photoOffsetX = 0f, photoOffsetY = 0f))
                } else {
                    Toast.makeText(context, if (isTelugu) "ఫోటో తెరవలేకపోయాం" else "Couldn't open that photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(bi("దేవుని వాక్యం", "Deity line"))
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = invitation.showDeityLine,
            onCheckedChange = { onChange(invitation.copy(showDeityLine = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleMaroon)
        )
    }
    if (invitation.showDeityLine) {
        Field(bi("పైభాగంలో", "Shown at the top"), invitation.deityLine) { onChange(invitation.copy(deityLine = it)) }
    }

    SectionTitle(bi("ఫోటో (ఐచ్ఛికం)", "Photo (optional)"))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = { pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            border = BorderStroke(1.dp, TempleGold)
        ) { Text(if (photo == null) bi("ఫోటో ఎంచుకోండి", "Choose photo") else bi("మార్చండి", "Change")) }
        if (photo != null) {
            TextButton(onClick = { onChange(invitation.copy(photoPath = "")) }) { Text(bi("తీసివేయి", "Remove")) }
        }
    }
    if (photo != null) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Chip(bi("వృత్తం", "Circle"), invitation.photoFrame == PhotoFrame.CIRCLE) { onChange(invitation.copy(photoFrame = PhotoFrame.CIRCLE)) }
            Chip(bi("ఆర్చ్", "Arch"), invitation.photoFrame == PhotoFrame.ARCH) { onChange(invitation.copy(photoFrame = PhotoFrame.ARCH)) }
        }
        Text(bi("లాగి సర్దండి, రెండు వేళ్లతో జూమ్ చేయండి", "Drag to move, pinch to zoom"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        PhotoFrameEditor(photo, invitation, onChange)
    }

    SectionTitle(bi("ఆహ్వాన వాక్యం", "Invitation text"))
    Field(
        label = bi("ఖాళీగా ఉంటే సాధారణ వాక్యం వాడతాం", "Leave empty to use a standard paragraph"),
        value = invitation.invitationText,
        singleLine = false
    ) { onChange(invitation.copy(invitationText = it)) }
    Button(
        onClick = {
            aiBusy = true
            aiMessage = null
            scope.launch {
                val telugu = invitation.language != InvitationLanguage.ENGLISH
                when (val result = InvitationAiService().write(invitation, telugu)) {
                    is InvitationAiResult.Success -> onChange(invitation.copy(invitationText = result.text))
                    InvitationAiResult.NotConfigured -> aiMessage = if (isTelugu) "AI ఇంకా అందుబాటులో లేదు." else "AI writing isn't available yet."
                    InvitationAiResult.Failed -> aiMessage = if (isTelugu) "AI వాక్యం రాయలేకపోయింది. మళ్లీ ప్రయత్నించండి." else "Couldn't write the text. Please try again."
                }
                aiBusy = false
            }
        },
        enabled = !aiBusy,
        colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon, contentColor = Color.White),
        modifier = Modifier.testTag("shubhalekha_ai")
    ) {
        if (aiBusy) {
            CircularProgressIndicator(color = TempleGold, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(bi("✨ AI తో ఆహ్వాన వాక్యం", "✨ Write invitation text with AI"))
    }
    aiMessage?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}

/** Drag and pinch inside the frame; the card uses the same scale and offsets. */
@Composable
private fun PhotoFrameEditor(photo: Bitmap, invitation: Invitation, onChange: (Invitation) -> Unit) {
    val latest by rememberUpdatedState(invitation)
    var size by remember { mutableStateOf(IntSize.Zero) }
    val shape = if (invitation.photoFrame == PhotoFrame.CIRCLE) CircleShape else ArchShape

    Box(
        modifier = Modifier
            .size(width = 200.dp, height = if (invitation.photoFrame == PhotoFrame.ARCH) 250.dp else 200.dp)
            .clip(shape)
            .border(3.dp, TempleGold, shape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan: Offset, zoom, _ ->
                    val current = latest
                    if (size.width == 0) return@detectTransformGestures
                    onChange(
                        current.copy(
                            photoScale = (current.photoScale * zoom).coerceIn(1f, 5f),
                            photoOffsetX = (current.photoOffsetX + pan.x / size.width).coerceIn(-1f, 1f),
                            photoOffsetY = (current.photoOffsetY + pan.y / size.height).coerceIn(-1f, 1f)
                        )
                    )
                }
            }
            .testTag("photo_frame_editor")
    ) {
        Image(
            bitmap = photo.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = invitation.photoScale,
                    scaleY = invitation.photoScale,
                    translationX = invitation.photoOffsetX * size.width,
                    translationY = invitation.photoOffsetY * size.height
                )
        )
    }
}

@Composable
private fun ExportStep(
    isTelugu: Boolean,
    invitation: Invitation,
    template: InvitationTemplate,
    unlocked: Boolean,
    store: InvitationStore,
    onUnlocked: () -> Unit,
    onShared: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }

    fun run(action: suspend () -> Unit) {
        busy = true
        scope.launch {
            try {
                action()
            } catch (e: Exception) {
                Toast.makeText(context, if (isTelugu) "ఎగుమతి విఫలమైంది" else "Export failed", Toast.LENGTH_SHORT).show()
            }
            busy = false
        }
    }

    Text(
        text = if (unlocked) {
            bi("ప్రీమియం: వాటర్‌మార్క్ లేకుండా HD చిత్రాలు, ప్రింట్ PDF", "Premium: HD images without watermark, print PDF")
        } else {
            bi("ఉచితం: 1080×1920 చిత్రాలు, చిన్న వాటర్‌మార్క్‌తో", "Free: 1080×1920 images with a small watermark")
        },
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Button(
        onClick = {
            run {
                val files = withContext(Dispatchers.Default) { InvitationExport.pngs(context, invitation, template, unlocked) }
                InvitationExport.share(context, files, "image/png")
                onShared()
            }
        },
        enabled = !busy,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("shubhalekha_share"),
        colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
    ) { Text(Translations.tr("share_on_whatsapp", if (isTelugu) Language.TELUGU else Language.ENGLISH), fontWeight = FontWeight.Bold) }

    OutlinedButton(
        onClick = {
            run {
                val files = withContext(Dispatchers.Default) { InvitationExport.pngs(context, invitation, template, unlocked) }
                val saved = withContext(Dispatchers.IO) { InvitationExport.saveToGallery(context, files) }
                Toast.makeText(context, if (saved) Translations.tr("image_saved_toast", if (isTelugu) Language.TELUGU else Language.ENGLISH) else "Could not save image to gallery", Toast.LENGTH_SHORT).show()
            }
        },
        enabled = !busy,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, TempleGold)
    ) { Text(Translations.tr("save_to_gallery", if (isTelugu) Language.TELUGU else Language.ENGLISH)) }

    if (unlocked) {
        OutlinedButton(
            onClick = {
                run {
                    val pdf = withContext(Dispatchers.Default) { InvitationExport.pdf(context, invitation, template) }
                    InvitationExport.share(context, listOf(pdf), "application/pdf")
                }
            },
            enabled = !busy,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shubhalekha_pdf"),
            border = BorderStroke(1.dp, TempleGold)
        ) { Text(bi("ప్రింట్ PDF (A5)", "Print-ready PDF (A5)")) }
    } else {
        Card(
            colors = CardDefaults.cardColors(containerColor = TempleGold.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, TempleGold)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(bi("ప్రీమియం శుభలేఖ", "Premium invitation"), fontWeight = FontWeight.Bold)
                Text(
                    text = bi(
                        "వాటర్‌మార్క్ లేదు • 2160×3840 HD చిత్రాలు • ప్రింట్‌కు A5 PDF",
                        "No watermark • 2160×3840 HD images • A5 PDF for printing"
                    ),
                    fontSize = 12.sp
                )
                Button(
                    onClick = {
                        store.unlockInvitation(invitation.id)
                        onUnlocked()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon, contentColor = Color.White),
                    modifier = Modifier.testTag("shubhalekha_unlock")
                ) { Text(bi("₹49 కి అన్‌లాక్ చేయండి", "Unlock for ₹49")) }
            }
        }
    }

    if (busy) CircularProgressIndicator(color = TempleGold)
}
