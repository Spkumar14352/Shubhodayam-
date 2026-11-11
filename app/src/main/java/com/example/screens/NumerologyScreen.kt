package com.example.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.ImageShareSheet
import com.example.data.model.Language
import com.example.data.model.NumerologyProfile
import com.example.data.numerology.CompatibilityResult
import com.example.data.numerology.CompatibilityStatus
import com.example.data.numerology.NumerologyCalculator
import com.example.data.numerology.NumerologyData
import com.example.data.numerology.NumerologyNumberInfo
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleSaffron
import com.example.utils.NumerologyShareCardGenerator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun NumerologyScreen(
    language: Language,
    profiles: List<NumerologyProfile>,
    selectedProfileId: String?,
    onSaveProfile: (NumerologyProfile) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onSelectProfile: (String) -> Unit,
    onCardShared: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val isTelugu = language == Language.TELUGU
    val context = LocalContext.current

    // Active profile or fallback to first, or create form
    val activeProfile = profiles.find { it.id == selectedProfileId } ?: profiles.firstOrNull()

    var showProfileEditor by remember { mutableStateOf(profiles.isEmpty()) }
    var editingProfile by remember { mutableStateOf<NumerologyProfile?>(null) }
    var showShareSheet by remember { mutableStateOf(false) }

    if (showShareSheet && activeProfile != null) {
        ImageShareSheet(
            language = language,
            title = if (isTelugu) "${activeProfile.fullName} సంఖ్యా శాస్త్ర కార్డు" else "${activeProfile.fullName} Numerology Card",
            shareText = if (isTelugu)
                "నా సంఖ్యా శాస్త్ర వివరాలు & అదృష్ట సంఖ్యలు – శుభోదయ యాప్ లో చూడండి!"
            else "My Vedic & Chaldean Numerology numbers – via Shubhodaya app!",
            onShared = onCardShared,
            onDismiss = { showShareSheet = false },
            createBitmap = {
                NumerologyShareCardGenerator.createCardBitmap(activeProfile, language)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("numerology_screen")
    ) {
        SubScreenHeader(
            title = if (isTelugu) "సంఖ్యా శాస్త్రం" else "Numerology",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile selector chips + "Add Member" button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "కుటుంబ ప్రొఫైల్స్" else "Family Profiles",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedButton(
                        onClick = {
                            editingProfile = null
                            showProfileEditor = true
                        },
                        modifier = Modifier.testTag("numerology_add_profile_btn"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTelugu) "+ కొత్త ప్రొఫైల్" else "+ New Profile", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (profiles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(profiles) { prof ->
                            val isSelected = prof.id == activeProfile?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onSelectProfile(prof.id)
                                    showProfileEditor = false
                                },
                                label = {
                                    Text(
                                        text = "${prof.label}: ${prof.fullName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("numerology_chip_${prof.id}")
                            )
                        }
                    }
                }
            }

            // Profile Input Form / Editor Card
            if (showProfileEditor || activeProfile == null) {
                item {
                    ProfileEditorCard(
                        language = language,
                        existing = editingProfile,
                        onCancel = {
                            if (profiles.isNotEmpty()) {
                                showProfileEditor = false
                                editingProfile = null
                            }
                        },
                        onSave = { saved ->
                            onSaveProfile(saved)
                            showProfileEditor = false
                            editingProfile = null
                        }
                    )
                }
            } else {
                // Active Profile Actions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activeProfile.fullName} (${activeProfile.label})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                            Text(
                                text = "${if (isTelugu) "జననం:" else "DOB:"} ${activeProfile.birthDate.format(dtf)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    editingProfile = activeProfile
                                    showProfileEditor = true
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onDeleteProfile(activeProfile.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { showShareSheet = true },
                                modifier = Modifier.size(36.dp).testTag("numerology_share_btn")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = TempleSaffron, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Calculations & Result Panels
                item {
                    val moolank = NumerologyCalculator.calculateMoolank(activeProfile.birthDate.dayOfMonth)
                    val bhagyankRes = NumerologyCalculator.calculateBhagyank(activeProfile.birthDate)
                    val nameRes = NumerologyCalculator.calculateNameNumber(activeProfile.fullName)

                    NumerologyResultContent(
                        language = language,
                        profile = activeProfile,
                        moolank = moolank,
                        bhagyankResult = bhagyankRes,
                        nameResult = nameRes,
                        onShare = { showShareSheet = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileEditorCard(
    language: Language,
    existing: NumerologyProfile?,
    onCancel: () -> Unit,
    onSave: (NumerologyProfile) -> Unit
) {
    val isTelugu = language == Language.TELUGU
    val context = LocalContext.current

    var label by remember { mutableStateOf(existing?.label ?: "Me") }
    var nameText by remember { mutableStateOf(existing?.fullName ?: "") }
    var birthDate by remember { mutableStateOf(existing?.birthDate ?: LocalDate.of(1990, 8, 29)) }
    var gender by remember { mutableStateOf(existing?.gender ?: "Unspecified") }
    var hasTeluguCharacters by remember { mutableStateOf(false) }

    val presetLabels = listOf("Me", "Spouse", "Son", "Daughter", "Father", "Mother")
    val presetLabelsTe = listOf("నేను (Me)", "భార్య/భర్త", "కుమారుడు", "కుమార్తె", "తండ్రి", "తల్లి")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("numerology_editor_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (existing == null) (if (isTelugu) "నూతన సంఖ్యా శాస్త్ర ప్రొఫైల్" else "New Numerology Profile")
                else (if (isTelugu) "ప్రొఫైల్ సవరణ" else "Edit Profile"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Member label selection
            Text(text = if (isTelugu) "సంబంధం / లేబుల్:" else "Relation / Label:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetLabels.indices.toList()) { idx ->
                    val lbl = presetLabels[idx]
                    val displayLbl = if (isTelugu) presetLabelsTe[idx] else lbl
                    val isSel = label.equals(lbl, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { label = lbl },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Text(
                            text = displayLbl,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Full Name Input
            OutlinedTextField(
                value = nameText,
                onValueChange = { input ->
                    nameText = input
                    // Check if input contains Telugu or non-latin characters
                    hasTeluguCharacters = input.any { it.code in 0x0C00..0x0C7F }
                },
                label = { Text(if (isTelugu) "పూర్తి పేరు (ఇంగ్లీషులో)" else "Full Name (English letters)") },
                placeholder = { Text("e.g. RAVI KUMAR") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("numerology_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (hasTeluguCharacters) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isTelugu)
                        "గమనిక: ధృవపత్రాలలో ఉండే ఇంగ్లీషు స్పెల్లింగ్ ప్రకారం పేరు నమోదు చేయండి (Chaldean system)"
                    else "Enter your name in English spelling as used in documents",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date of Birth picker
            Text(text = if (isTelugu) "జనన తేదీ:" else "Date of Birth:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            val dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                birthDate = LocalDate.of(y, m + 1, d)
                            },
                            birthDate.year,
                            birthDate.monthValue - 1,
                            birthDate.dayOfMonth
                        ).show()
                    }
                    .testTag("numerology_dob_picker"),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = birthDate.format(dtf), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gender selection (Optional)
            Text(text = if (isTelugu) "లింగం (ఐచ్ఛికం):" else "Gender (Optional):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Pair("Male", if (isTelugu) "పురుషుడు" else "Male"),
                    Pair("Female", if (isTelugu) "మహిళ" else "Female"),
                    Pair("Unspecified", if (isTelugu) "పేర్కొనలేదు" else "Unspecified")
                ).forEach { (code, labelText) ->
                    val isSel = gender == code
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { gender = code },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = labelText,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isTelugu) "రద్దు చేయి" else "Cancel")
                }

                Button(
                    onClick = {
                        val trimmedName = nameText.trim()
                        if (trimmedName.isNotBlank()) {
                            val profileToSave = existing?.copy(
                                label = label,
                                fullName = trimmedName,
                                birthDate = birthDate,
                                gender = gender
                            ) ?: NumerologyProfile(
                                id = UUID.randomUUID().toString(),
                                label = label,
                                fullName = trimmedName,
                                birthDate = birthDate,
                                gender = gender
                            )
                            onSave(profileToSave)
                        }
                    },
                    enabled = nameText.trim().isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("numerology_save_profile_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (isTelugu) "భద్రపరచండి" else "Save Profile", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NumerologyResultContent(
    language: Language,
    profile: NumerologyProfile,
    moolank: Int,
    bhagyankResult: NumerologyCalculator.BhagyankResult,
    nameResult: NumerologyCalculator.NameNumberResult,
    onShare: () -> Unit
) {
    val isTelugu = language == Language.TELUGU
    val today = LocalDate.now()

    val moolankInfo = NumerologyData.getInfo(moolank)
    val bhagyankInfo = NumerologyData.getInfo(bhagyankResult.singleDigit)
    val nameInfo = NumerologyData.getInfo(nameResult.singleDigit)

    val personalYear = NumerologyCalculator.calculatePersonalYear(
        profile.birthDate.dayOfMonth,
        profile.birthDate.monthValue,
        today.year
    )
    val personalDay = NumerologyCalculator.calculatePersonalDay(personalYear, today)
    val dailyTip = NumerologyData.getDailyTip(personalDay)

    val compatibility = NumerologyData.evaluateCompatibility(
        nameNumber = nameResult.singleDigit,
        moolank = moolank,
        bhagyank = bhagyankResult.singleDigit
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Three Big Cards (Moolank, Bhagyank, Name Number)
        Text(
            text = if (isTelugu) "మీ ప్రధాన సంఖ్యలు (స్పర్శించి వివరాలు చూడండి)" else "Your Core Numbers (Tap to expand)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExpandableNumberCard(
            title = if (isTelugu) "మూలాంకం (జనన సంఖ్య / Psychic Number)" else "Moolank (Psychic / Birth Number)",
            subtitle = if (isTelugu) "జన్మదిన తేదీ అంకెల మొత్తం: ${profile.birthDate.dayOfMonth}" else "Digit sum of birth day: ${profile.birthDate.dayOfMonth}",
            number = moolank.toString(),
            info = moolankInfo,
            language = language,
            testTag = "card_moolank"
        )

        val bhagyankDisplay = if (bhagyankResult.masterNumber != null) {
            "${bhagyankResult.singleDigit} (Master ${bhagyankResult.masterNumber})"
        } else {
            bhagyankResult.singleDigit.toString()
        }
        ExpandableNumberCard(
            title = if (isTelugu) "భాగ్యాంకం (విధి సంఖ్య / Destiny Number)" else "Bhagyank (Destiny / Life Path Number)",
            subtitle = if (isTelugu) "పూర్తి జనన తేదీ అంకెల మొత్తం: ${bhagyankResult.compoundTotal}" else "Digit sum of full date: ${bhagyankResult.compoundTotal}",
            number = bhagyankDisplay,
            info = bhagyankInfo,
            language = language,
            testTag = "card_bhagyank"
        )

        ExpandableNumberCard(
            title = if (isTelugu) "నామ సంఖ్య (Chaldean Name Number)" else "Name Number (Chaldean System)",
            subtitle = if (isTelugu) "అక్షర విలువల మొత్తం: ${nameResult.compoundTotal} → ${nameResult.singleDigit}" else "Letter sum: ${nameResult.compoundTotal} → ${nameResult.singleDigit}",
            number = "${nameResult.singleDigit} (${nameResult.compoundTotal})",
            info = nameInfo,
            language = language,
            testTag = "card_name_number"
        )

        // 2. "Your Lucky" Row (Days, Colors, Numbers)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TempleGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "మీ అదృష్ట అంశాలు (Lucky Elements)" else "Your Lucky Elements",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LuckyElementRow(
                    label = if (isTelugu) "అదృష్ట రోజులు" else "Lucky Days",
                    value = if (isTelugu) moolankInfo.luckyDaysTe.joinToString() else moolankInfo.luckyDaysEn.joinToString()
                )
                Spacer(modifier = Modifier.height(6.dp))
                LuckyElementRow(
                    label = if (isTelugu) "అదృష్ట రంగులు" else "Lucky Colors",
                    value = if (isTelugu) moolankInfo.luckyColorsTe.joinToString() else moolankInfo.luckyColorsEn.joinToString()
                )
                Spacer(modifier = Modifier.height(6.dp))
                LuckyElementRow(
                    label = if (isTelugu) "అదృష్ట సంఖ్యలు" else "Lucky Numbers",
                    value = moolankInfo.luckyNumbers.joinToString()
                )
                Spacer(modifier = Modifier.height(6.dp))
                LuckyElementRow(
                    label = if (isTelugu) "సంప్రదాయ రత్నం" else "Gemstone",
                    value = if (isTelugu) moolankInfo.gemstoneTe else moolankInfo.gemstoneEn
                )
            }
        }

        // 3. "Today for you" Card (Personal day number + daily tip)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, TempleSaffron.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = TempleSaffron, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${if (isTelugu) "నేటి మార్గదర్శనం" else "Today for You"} (Personal Day $personalDay)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isTelugu) dailyTip.first else dailyTip.second,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${if (isTelugu) "వ్యక్తిగత సంవత్సరం (Personal Year):" else "Personal Year:"} $personalYear",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4. Name Compatibility Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "పేరు అనుకూలత (Name Compatibility)" else "Name Compatibility",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val (badgeText, badgeColor) = when (compatibility.status) {
                        CompatibilityStatus.EXCELLENT -> (if (isTelugu) "అత్యుత్తమం" else "Excellent") to Color(0xFF2E7D32)
                        CompatibilityStatus.GOOD -> (if (isTelugu) "మంచిది" else "Good") to Color(0xFF2E7D32)
                        CompatibilityStatus.NEUTRAL -> (if (isTelugu) "సమతౌల్యం" else "Neutral") to Color(0xFFF28C28)
                        CompatibilityStatus.NEEDS_ATTENTION -> (if (isTelugu) "పరిశీలన అవసరం" else "Needs Attention") to Color(0xFFC62828)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, badgeColor)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isTelugu) compatibility.explanationTe else compatibility.explanationEn,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 5. Name spelling ideas (Optional, subtle variations)
                val targetFriendly = moolankInfo.friendlyNumbers.toSet()
                val variations = remember(profile.fullName) {
                    NumerologyCalculator.suggestNameVariations(profile.fullName, targetFriendly)
                }

                if (variations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isTelugu) "స్పెల్లింగ్ ఆలోచనలు (అనుకూల సంఖ్యల కొరకు):" else "Subtle Spelling Ideas (for target friendly numbers):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    variations.forEach { (altName, altRes) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• $altName", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "Total ${altRes.compoundTotal} → ${altRes.singleDigit}", fontSize = 11.sp, color = TempleSaffron)
                        }
                    }
                }
            }
        }

        // Big Saffron Share Button
        Button(
            onClick = onShare,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("numerology_share_card_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTelugu) "నా సంఖ్యల కార్డును షేర్ చేయండి" else "Share My Numbers Card",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 6. Disclaimer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = if (isTelugu)
                    "గమనిక: ఈ సమాచారం కేవలం వినోదం మరియు సంప్రదాయ జ్ఞానం కొరకు మాత్రమే. వృత్తిపరమైన లేదా వైద్య సలహాలకు ఇది ప్రత్యామ్నాయం కాదు."
                else "Disclaimer: For entertainment and traditional guidance only. Not a substitute for professional advice.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun ExpandableNumberCard(
    title: String,
    subtitle: String,
    number: String,
    info: NumerologyNumberInfo,
    language: Language,
    testTag: String
) {
    val isTelugu = language == Language.TELUGU
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TempleSaffron
                    )
                }

                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${if (isTelugu) "అధిపతి గ్రహం:" else "Ruling Planet:"} ${if (isTelugu) info.planetNameTe else info.planetNameEn}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TempleGold
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = if (isTelugu) info.natureTe else info.natureEn,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTelugu) "బలాలు (Key Strengths):" else "Strengths:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val strengths = if (isTelugu) info.strengthsTe else info.strengthsEn
                    strengths.forEach { s ->
                        Text(text = "• $s", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTelugu) "సమతుల్యత కొరకు (To Watch):" else "Things to Watch:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val toWatch = if (isTelugu) info.toWatchTe else info.toWatchEn
                    toWatch.forEach { w ->
                        Text(text = "• $w", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun LuckyElementRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}
