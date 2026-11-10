@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.shareCardViaWhatsApp
import com.example.data.PanchangNames
import com.example.data.model.AppConfig
import com.example.data.model.BabyGender
import com.example.data.model.BabyNameFilters
import com.example.data.model.BabyNameSearch
import com.example.data.model.BirthStar
import com.example.data.model.City
import com.example.data.model.CityData
import com.example.data.model.Language
import com.example.data.model.ShortlistedName
import com.example.i18n.Translations
import com.example.services.AiBabyName
import com.example.services.BabyNamesAiResult
import com.example.services.BabyNamesAiService
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.BabyNamesShareCardGenerator
import com.example.utils.PanchangShareCardGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class BabyNamesView { FINDER, RESULTS, SHORTLIST }

private val BABY_NAME_TAGS = listOf("God", "Nature", "Modern", "Traditional")

@Composable
fun BabyNamesScreen(
    language: Language,
    defaultCity: City,
    shortlist: List<ShortlistedName>,
    onToggleShortlist: (ShortlistedName) -> Unit,
    onCardShared: () -> Unit,
    onBack: () -> Unit
) {
    var view by remember { mutableStateOf(BabyNamesView.FINDER) }
    var star by remember { mutableStateOf<BirthStar?>(null) }

    val leaveCurrentView = {
        when {
            view == BabyNamesView.FINDER -> onBack()
            view == BabyNamesView.SHORTLIST && star != null -> view = BabyNamesView.RESULTS
            else -> view = BabyNamesView.FINDER
        }
    }
    BackHandler(onBack = leaveCurrentView)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("baby_names_screen")
    ) {
        SubScreenHeader(title = Translations.tr("menu_baby_names", language), onBack = leaveCurrentView)

        when (view) {
            BabyNamesView.FINDER -> FinderView(
                language = language,
                defaultCity = defaultCity,
                shortlistSize = shortlist.size,
                onFound = {
                    star = it
                    view = BabyNamesView.RESULTS
                },
                onOpenShortlist = { view = BabyNamesView.SHORTLIST }
            )

            BabyNamesView.RESULTS -> star?.let {
                ResultsView(
                    language = language,
                    star = it,
                    shortlist = shortlist,
                    onToggleShortlist = onToggleShortlist,
                    onOpenShortlist = { view = BabyNamesView.SHORTLIST }
                )
            }

            BabyNamesView.SHORTLIST -> ShortlistView(
                language = language,
                star = star,
                shortlist = shortlist,
                onToggleShortlist = onToggleShortlist,
                onCardShared = onCardShared
            )
        }
    }
}

@Composable
private fun FinderView(
    language: Language,
    defaultCity: City,
    shortlistSize: Int,
    onFound: (BirthStar) -> Unit,
    onOpenShortlist: () -> Unit
) {
    var useBirthDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = Translations.tr("baby_intro", language),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedButton(
            onClick = onOpenShortlist,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("baby_open_shortlist"),
            border = BorderStroke(1.dp, TempleGold)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = TempleSaffron)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${Translations.tr("baby_shortlist", language)} ($shortlistSize)",
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChoiceChip(
                label = Translations.tr("baby_mode_known", language),
                selected = !useBirthDetails,
                onClick = { useBirthDetails = false },
                modifier = Modifier
                    .weight(1f)
                    .testTag("baby_mode_known")
            )
            ChoiceChip(
                label = Translations.tr("baby_mode_birth", language),
                selected = useBirthDetails,
                onClick = { useBirthDetails = true },
                modifier = Modifier
                    .weight(1f)
                    .testTag("baby_mode_birth")
            )
        }

        if (useBirthDetails) {
            BirthDetailsForm(language, defaultCity, onFound)
        } else {
            KnownNakshatraForm(language, onFound)
        }
    }
}

private fun nakshatraName(number: Int, language: Language): String {
    val entry = PanchangNames.getNakshatra(number)
    return if (language == Language.TELUGU) entry.nameTe else entry.nameEn
}

@Composable
private fun KnownNakshatraForm(language: Language, onFound: (BirthStar) -> Unit) {
    var nakshatra by remember { mutableStateOf<Int?>(null) }
    var pada by remember { mutableStateOf<Int?>(null) }
    val anyPada = Translations.tr("baby_any_pada", language)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DropdownField(
            label = Translations.tr("baby_nakshatra", language),
            value = nakshatra?.let { nakshatraName(it, language) },
            placeholder = Translations.tr("baby_tap_to_select", language),
            options = (1..27).toList(),
            optionLabel = { nakshatraName(it, language) },
            onSelect = { nakshatra = it },
            modifier = Modifier.testTag("baby_nakshatra_dropdown")
        )
        DropdownField<Int?>(
            label = Translations.tr("baby_pada_optional", language),
            value = pada?.toString(),
            placeholder = anyPada,
            options = listOf(null, 1, 2, 3, 4),
            optionLabel = { it?.toString() ?: anyPada },
            onSelect = { pada = it },
            modifier = Modifier.testTag("baby_pada_dropdown")
        )
        PrimaryAction(
            text = Translations.tr("baby_show_names", language),
            enabled = nakshatra != null,
            onClick = { nakshatra?.let { onFound(BirthStar(it, pada)) } },
            testTag = "baby_show_names"
        )
    }
}

@Composable
private fun BirthDetailsForm(
    language: Language,
    defaultCity: City,
    onFound: (BirthStar) -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var city by remember { mutableStateOf(defaultCity) }
    val placeholder = Translations.tr("baby_tap_to_select", language)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SelectableField(
            label = Translations.tr("baby_birth_date", language),
            value = date?.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US)),
            placeholder = placeholder,
            onClick = {
                val today = LocalDate.now(ZoneId.systemDefault())
                val initial = date ?: today
                DatePickerDialog(
                    context,
                    { _, year, month, day -> date = LocalDate.of(year, month + 1, day) },
                    initial.year,
                    initial.monthValue - 1,
                    initial.dayOfMonth
                ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
            },
            modifier = Modifier.testTag("baby_birth_date")
        )
        SelectableField(
            label = Translations.tr("baby_birth_time", language),
            value = time?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US)),
            placeholder = placeholder,
            onClick = {
                val initial = time ?: LocalTime.NOON
                TimePickerDialog(
                    context,
                    { _, hour, minute -> time = LocalTime.of(hour, minute) },
                    initial.hour,
                    initial.minute,
                    false
                ).show()
            },
            modifier = Modifier.testTag("baby_birth_time")
        )
        DropdownField(
            label = Translations.tr("baby_birth_place", language),
            value = if (language == Language.TELUGU) city.nameTe else city.nameEn,
            placeholder = placeholder,
            options = CityData.cities,
            optionLabel = { if (language == Language.TELUGU) it.nameTe else it.nameEn },
            onSelect = { city = it },
            modifier = Modifier.testTag("baby_birth_place")
        )
        PrimaryAction(
            text = Translations.tr("baby_find_nakshatra", language),
            enabled = date != null && time != null,
            onClick = {
                val birthDate = date ?: return@PrimaryAction
                val birthTime = time ?: return@PrimaryAction
                onFound(BabyNameSearch.birthStarAt(LocalDateTime.of(birthDate, birthTime)))
            },
            testTag = "baby_find_nakshatra"
        )
    }
}

@Composable
private fun ResultsView(
    language: Language,
    star: BirthStar,
    shortlist: List<ShortlistedName>,
    onToggleShortlist: (ShortlistedName) -> Unit,
    onOpenShortlist: () -> Unit
) {
    val isTelugu = language == Language.TELUGU
    val scope = rememberCoroutineScope()
    val aiService = remember { BabyNamesAiService() }
    val syllables = remember(star) { BabyNameSearch.syllablesFor(star) }

    var gender by remember(star) { mutableStateOf<BabyGender?>(null) }
    var syllable by remember(star) { mutableStateOf(BabyNameSearch.namakshara(star)?.en) }
    var tags by remember(star) { mutableStateOf(emptySet<String>()) }
    var shortOnly by remember(star) { mutableStateOf(false) }

    var aiNames by remember(star, syllable, gender) { mutableStateOf(emptyList<AiBabyName>()) }
    var aiLoading by remember(star, syllable, gender) { mutableStateOf(false) }
    var aiMessage by remember(star, syllable, gender) { mutableStateOf<String?>(null) }

    val names = remember(star, gender, syllable, tags, shortOnly) {
        BabyNameSearch.search(star, BabyNameFilters(gender, syllable, tags, shortOnly))
    }
    val savedNames = shortlist.map { it.name.lowercase() }.toSet()
    val visibleAiNames = aiNames.filter {
        !shortOnly || it.name.length <= BabyNameSearch.SHORT_NAME_MAX_LETTERS
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("baby_results"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { StarSummary(language, star) }

        item {
            OutlinedButton(
                onClick = onOpenShortlist,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, TempleGold)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = TempleSaffron)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${Translations.tr("baby_shortlist", language)} (${shortlist.size})",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            FilterGroup(Translations.tr("baby_filter_gender", language)) {
                listOf(
                    BabyGender.BOY to "baby_boy",
                    BabyGender.GIRL to "baby_girl",
                    BabyGender.UNISEX to "baby_unisex"
                ).forEach { (option, key) ->
                    ChoiceChip(
                        label = Translations.tr(key, language),
                        selected = gender == option,
                        onClick = { gender = if (gender == option) null else option },
                        modifier = Modifier.testTag("baby_filter_$key")
                    )
                }
            }
        }

        item {
            FilterGroup(Translations.tr("baby_filter_syllable", language)) {
                syllables.forEach { option ->
                    ChoiceChip(
                        label = if (isTelugu) "${option.te} (${option.en})" else option.en,
                        selected = syllable == option.en,
                        onClick = { syllable = if (syllable == option.en) null else option.en },
                        modifier = Modifier.testTag("baby_syllable_${option.en}")
                    )
                }
            }
        }

        item {
            FilterGroup(Translations.tr("baby_filter_tags", language)) {
                BABY_NAME_TAGS.forEach { tag ->
                    ChoiceChip(
                        label = Translations.tr("baby_tag_${tag.lowercase()}", language),
                        selected = tag in tags,
                        onClick = { tags = if (tag in tags) tags - tag else tags + tag }
                    )
                }
                ChoiceChip(
                    label = Translations.tr("baby_short_names", language),
                    selected = shortOnly,
                    onClick = { shortOnly = !shortOnly },
                    modifier = Modifier.testTag("baby_short_names")
                )
            }
        }

        if (names.isEmpty()) {
            item {
                Text(
                    text = Translations.tr("baby_no_names", language),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        items(names, key = { it.name }) { name ->
            val item = ShortlistedName(name.name, name.telugu, name.meaningEn, name.meaningTe, false)
            NameCard(language, item, name.name.lowercase() in savedNames) { onToggleShortlist(item) }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        val selected = syllable ?: return@Button
                        aiLoading = true
                        aiMessage = null
                        scope.launch {
                            when (val result = aiService.suggestNames(selected, gender, language)) {
                                is BabyNamesAiResult.Success -> {
                                    val known = names.map { it.name.lowercase() }.toSet()
                                    aiNames = result.names.filter { it.name.lowercase() !in known }
                                }
                                BabyNamesAiResult.NotConfigured ->
                                    aiMessage = Translations.tr("baby_ai_not_set_up", language)
                                BabyNamesAiResult.Failed ->
                                    aiMessage = Translations.tr("baby_ai_failed", language)
                            }
                            aiLoading = false
                        }
                    },
                    enabled = syllable != null && !aiLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("baby_ai_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon, contentColor = Color.White)
                ) {
                    if (aiLoading) {
                        CircularProgressIndicator(
                            color = TempleGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(Translations.tr("baby_ai_button", language), fontWeight = FontWeight.Bold)
                }
                val hint = aiMessage ?: if (syllable == null) Translations.tr("baby_ai_pick_syllable", language) else null
                hint?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("baby_ai_message")
                    )
                }
            }
        }

        items(visibleAiNames, key = { "ai_${it.name}" }) { name ->
            val item = ShortlistedName(
                name = name.name,
                telugu = name.telugu,
                meaningEn = if (isTelugu) "" else name.meaning,
                meaningTe = if (isTelugu) name.meaning else "",
                isAiSuggested = true
            )
            NameCard(language, item, name.name.lowercase() in savedNames) { onToggleShortlist(item) }
        }
    }
}

@Composable
private fun StarSummary(language: Language, star: BirthStar) {
    val isTelugu = language == Language.TELUGU
    val namakshara = BabyNameSearch.namakshara(star)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("baby_star_summary"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TempleMaroon)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = nakshatraName(star.nakshatraNumber, language),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TempleGold
            )
            star.pada?.let {
                SummaryLine("${Translations.tr("baby_pada", language)}: $it")
            }
            star.rasi?.let {
                SummaryLine("${Translations.tr("baby_rasi", language)}: ${if (isTelugu) it.nameTe else it.nameEn}")
            }
            namakshara?.let {
                SummaryLine("${Translations.tr("baby_namakshara", language)}: ${if (isTelugu) "${it.te} (${it.en})" else it.en}")
            }
            Text(
                text = Translations.tr("baby_verify_note", language),
                fontSize = 11.sp,
                color = Color(0xFFF2D37A)
            )
        }
    }
}

@Composable
private fun SummaryLine(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color(0xFFFFF7EC))
}

@Composable
private fun FilterGroup(title: String, chips: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { chips() }
    }
}

@Composable
private fun NameCard(
    language: Language,
    item: ShortlistedName,
    saved: Boolean,
    onToggle: () -> Unit
) {
    val isTelugu = language == Language.TELUGU
    val meaning = if (isTelugu) item.meaningTe.ifBlank { item.meaningEn } else item.meaningEn.ifBlank { item.meaningTe }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("baby_name_${item.name}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isTelugu) item.telugu else item.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) item.name else item.telugu,
                        fontSize = 13.sp,
                        color = TempleGold
                    )
                }
                Text(
                    text = meaning,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.isAiSuggested) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TempleSaffron.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = Translations.tr("baby_ai_tag", language),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TempleSaffron,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            IconButton(
                onClick = onToggle,
                modifier = Modifier.testTag("baby_heart_${item.name}")
            ) {
                Icon(
                    imageVector = if (saved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = Translations.tr("baby_shortlist", language),
                    tint = TempleSaffron
                )
            }
        }
    }
}

@Composable
private fun ShortlistView(
    language: Language,
    star: BirthStar?,
    shortlist: List<ShortlistedName>,
    onToggleShortlist: (ShortlistedName) -> Unit,
    onCardShared: () -> Unit
) {
    var showShareSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("baby_shortlist"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (shortlist.isEmpty()) {
            item {
                Text(
                    text = Translations.tr("baby_shortlist_empty", language),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )
            }
        } else {
            item {
                PrimaryAction(
                    text = Translations.tr("baby_share_shortlist", language),
                    enabled = true,
                    onClick = { showShareSheet = true },
                    testTag = "baby_share_shortlist"
                )
            }
            items(shortlist, key = { it.name }) { item ->
                NameCard(language, item, saved = true) { onToggleShortlist(item) }
            }
        }
    }

    if (showShareSheet) {
        ShortlistShareSheet(
            language = language,
            title = shareCardTitle(star, language),
            shortlist = shortlist,
            onCardShared = onCardShared,
            onDismiss = { showShareSheet = false }
        )
    }
}

private fun shareCardTitle(star: BirthStar?, language: Language): String {
    if (star == null) return Translations.tr("baby_card_title_generic", language)
    return String.format(
        Translations.tr("baby_card_title", language),
        nakshatraName(star.nakshatraNumber, language)
    )
}

@Composable
private fun ShortlistShareSheet(
    language: Language,
    title: String,
    shortlist: List<ShortlistedName>,
    onCardShared: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(shortlist, title, language) {
        bitmap = withContext(Dispatchers.Default) {
            BabyNamesShareCardGenerator.createCardBitmap(shortlist, title, language)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("baby_share_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(4f / 5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TempleMaroon),
                contentAlignment = Alignment.Center
            ) {
                val preview = bitmap
                if (preview == null) {
                    CircularProgressIndicator(color = TempleGold)
                } else {
                    Image(
                        bitmap = preview.asImageBitmap(),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Button(
                onClick = {
                    val image = bitmap ?: return@Button
                    shareCardViaWhatsApp(
                        context = context,
                        bitmap = image,
                        shareText = "$title – Shubhodaya app: ${AppConfig.PLAY_STORE_URL}",
                        chooserTitle = title
                    )
                    onCardShared()
                    onDismiss()
                },
                enabled = bitmap != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("baby_share_confirm"),
                colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(Translations.tr("share_on_whatsapp", language), fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    val image = bitmap ?: return@OutlinedButton
                    val saved = PanchangShareCardGenerator.saveBitmapToGallery(context, image)
                    if (saved) {
                        Toast.makeText(context, Translations.tr("image_saved_toast", language), Toast.LENGTH_LONG).show()
                        onCardShared()
                    } else {
                        Toast.makeText(context, "Could not save image to gallery", Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                },
                enabled = bitmap != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.dp, TempleGold)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = TempleGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Translations.tr("save_to_gallery", language), color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TempleMaroon,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun PrimaryAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag(testTag),
        colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SelectableField(
    label: String,
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value ?: placeholder,
                    fontSize = 15.sp,
                    fontWeight = if (value == null) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (value == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
            }
            if (showArrow) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TempleGold)
            }
        }
    }
}

@Composable
private fun <T> DropdownField(
    label: String,
    value: String?,
    placeholder: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        SelectableField(
            label = label,
            value = value,
            placeholder = placeholder,
            onClick = { expanded = true },
            showArrow = true
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 340.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
