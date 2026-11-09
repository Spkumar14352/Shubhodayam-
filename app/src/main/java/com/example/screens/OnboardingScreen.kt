package com.example.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.City
import com.example.data.model.CityData
import com.example.data.model.Language
import com.example.data.model.Rasi
import com.example.data.model.RasiData
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleMaroonLight
import com.example.ui.theme.TempleSaffron
import com.example.ui.theme.TempleWarmCream

@Composable
fun OnboardingScreen(
    currentStep: Int,
    selectedLanguage: Language,
    selectedCity: City,
    selectedRasi: Rasi,
    onLanguageChange: (Language) -> Unit,
    onCityChange: (City) -> Unit,
    onRasiChange: (Rasi) -> Unit,
    onStepChange: (Int) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header with App Identity & Step Indicator
            OnboardingHeader(
                currentStep = currentStep,
                language = selectedLanguage
            )

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentStep) {
                    1 -> LanguageSelectionStep(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = onLanguageChange,
                        onNext = { onStepChange(2) }
                    )
                    2 -> LocationSelectionStep(
                        language = selectedLanguage,
                        selectedCity = selectedCity,
                        onCityChange = onCityChange,
                        onBack = { onStepChange(1) },
                        onNext = { onStepChange(3) }
                    )
                    3 -> RasiSelectionStep(
                        language = selectedLanguage,
                        selectedRasi = selectedRasi,
                        onRasiChange = onRasiChange,
                        onBack = { onStepChange(2) },
                        onFinish = onComplete,
                        onSkip = onComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentStep: Int,
    language: Language
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TempleMaroonDark, TempleMaroon)
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(2.dp, TempleGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.shubhodaya_icon),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = Translations.tr("app_name", language),
            color = TempleGoldLight,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Text(
            text = Translations.tr("tagline", language),
            color = Color(0xFFF3E2E4),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(1, 2, 3).forEach { step ->
                val isActive = step == currentStep
                val isDone = step < currentStep

                Box(
                    modifier = Modifier
                        .size(if (isActive) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isActive -> TempleGold
                                isDone -> TempleSaffron
                                else -> Color(0x55FFFFFF)
                            }
                        )
                )

                if (step < 3) {
                    Spacer(
                        modifier = Modifier
                            .width(28.dp)
                            .height(2.dp)
                            .background(if (step < currentStep) TempleGold else Color(0x33FFFFFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectionStep(
    selectedLanguage: Language,
    onLanguageChange: (Language) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = Translations.tr("onboard_step1_title", selectedLanguage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = Translations.tr("onboard_step1_desc", selectedLanguage),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Telugu Option Card
            LanguageOptionCard(
                language = Language.TELUGU,
                isSelected = selectedLanguage == Language.TELUGU,
                onSelect = { onLanguageChange(Language.TELUGU) },
                nativeName = "తెలుగు",
                englishName = "Telugu (Default)",
                description = "పంచాంగం, తిథి, నక్షత్రాలు మరియు వార్తలు సంపూర్ణంగా తెలుగులో",
                testTag = "lang_option_telugu"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // English Option Card
            LanguageOptionCard(
                language = Language.ENGLISH,
                isSelected = selectedLanguage == Language.ENGLISH,
                onSelect = { onLanguageChange(Language.ENGLISH) },
                nativeName = "English",
                englishName = "English",
                description = "Full Panchang, planetary timings & news in English",
                testTag = "lang_option_english"
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("onboarding_lang_next_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = TempleMaroon,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = Translations.tr("next", selectedLanguage),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LanguageOptionCard(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit,
    nativeName: String,
    englishName: String,
    description: String,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = TempleGold,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nativeName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($englishName)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun LocationSelectionStep(
    language: Language,
    selectedCity: City,
    onCityChange: (City) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var gpsNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = Translations.tr("onboard_step2_title", language),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = Translations.tr("onboard_step2_desc", language),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // GPS Location Button
        Button(
            onClick = {
                // Select default or nearest city
                onCityChange(CityData.defaultCity)
                gpsNotice = Translations.tr("onboard_step2_gps_success", language) + ": " +
                        (if (language == Language.TELUGU) CityData.defaultCity.nameTe else CityData.defaultCity.nameEn)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("onboard_gps_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = TempleGold,
                contentColor = TempleMaroonDark
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Translations.tr("onboard_step2_gps_btn", language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        gpsNotice?.let { notice ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = notice,
                color = Color(0xFF2E7D32),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cities List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CityData.cities) { city ->
                val isSelected = selectedCity.id == city.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCityChange(city) }
                        .testTag("city_item_${city.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) TempleSaffron else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == Language.TELUGU) city.nameTe else city.nameEn,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == Language.TELUGU) "${city.nameEn} • ${city.stateTe}" else "${city.nameTe} • ${city.stateEn}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = TempleGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("onboard_city_back_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.tr("back", language),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("onboard_city_next_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TempleMaroon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.tr("next", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RasiSelectionStep(
    language: Language,
    selectedRasi: Rasi,
    onRasiChange: (Rasi) -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Translations.tr("onboard_step3_title", language),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = Translations.tr("onboard_step3_desc", language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = Translations.tr("skip", language),
                color = TempleSaffron,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onSkip() }
                    .padding(8.dp)
                    .testTag("onboard_skip_rasi_btn")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column Grid for 12 Rasis
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(RasiData.allRasis) { rasi ->
                val isSelected = selectedRasi.id == rasi.id
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onRasiChange(rasi) }
                        .testTag("rasi_item_${rasi.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TempleMaroon else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = rasi.symbol,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.TELUGU) rasi.nameTe else rasi.nameEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) TempleGoldLight else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (language == Language.TELUGU) rasi.nameEn else rasi.nameTe,
                            fontSize = 11.sp,
                            color = if (isSelected) Color(0xFFE2D4D7) else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("onboard_rasi_back_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.tr("back", language),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("onboard_finish_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TempleMaroon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = Translations.tr("get_started", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
