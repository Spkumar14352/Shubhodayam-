package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AiPanditMessage
import com.example.data.model.City
import com.example.data.model.Language
import com.example.data.model.MoreDestination
import com.example.data.model.RadioStation
import com.example.data.model.Rasi
import com.example.data.model.ThemeMode
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron

data class MoreGridItem(
    val destination: MoreDestination,
    val translationKey: String,
    val icon: ImageVector,
    val color: Color,
    val subtitleTe: String,
    val subtitleEn: String,
    val testTag: String
)

@Composable
fun MoreScreen(
    currentDestination: MoreDestination,
    language: Language,
    selectedCity: City,
    selectedRasi: Rasi,
    themeMode: ThemeMode,
    notificationsEnabled: Boolean,
    selectedRadioCategory: String,
    radioStations: List<com.example.data.model.RadioBrowserStation>,
    isRadioLoadingStations: Boolean,
    radioPlayerState: com.example.services.RadioPlayerState,
    cricketMatches: List<com.example.data.model.LiveCricketMatch>,
    isCricketLoading: Boolean,
    cricketMinutesAgo: Long,
    cricketMessage: String?,
    onRefreshCricket: () -> Unit,
    aiPanditMessages: List<AiPanditMessage>,
    isPanditThinking: Boolean,
    onNavigate: (MoreDestination) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (Language) -> Unit,
    onCityChange: (City) -> Unit,
    onRasiChange: (Rasi) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    morningNotifEnabled: Boolean = true,
    morningNotifTime: String = "06:30",
    rahuReminderEnabled: Boolean = true,
    festivalReminderEnabled: Boolean = true,
    onMorningNotifToggle: (Boolean) -> Unit = {},
    onMorningTimeChange: (String) -> Unit = {},
    onRahuReminderToggle: (Boolean) -> Unit = {},
    onFestivalReminderToggle: (Boolean) -> Unit = {},
    onTestNotification: (String) -> Unit = {},
    onSelectRadioCategory: (String) -> Unit,
    onSelectRadioStation: (com.example.data.model.RadioBrowserStation) -> Unit,
    onToggleRadioPlay: () -> Unit,
    onSendPanditMessage: (String) -> Unit,
    savedGreetingName: String = "",
    savedCreations: List<com.example.data.model.SavedGreeting> = emptyList(),
    onSaveGreetingName: (String) -> Unit = {},
    onSaveCreation: (com.example.data.model.SavedGreeting) -> Unit = {},
    onDeleteCreation: (String) -> Unit = {},
    onShowMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // If a sub-destination is selected, show that screen
    when (currentDestination) {
        MoreDestination.RADIO -> RadioSubScreen(
            language = language,
            selectedCategory = selectedRadioCategory,
            stations = radioStations,
            isLoading = isRadioLoadingStations,
            playerState = radioPlayerState,
            onSelectCategory = onSelectRadioCategory,
            onSelectStation = onSelectRadioStation,
            onTogglePlay = onToggleRadioPlay,
            onBack = onBack
        )
        MoreDestination.CRICKET -> CricketSubScreen(
            language = language,
            matches = cricketMatches,
            isLoading = isCricketLoading,
            minutesAgo = cricketMinutesAgo,
            infoMessage = cricketMessage,
            onRefresh = onRefreshCricket,
            onBack = onBack
        )
        MoreDestination.HOROSCOPE -> HoroscopeSubScreen(
            language = language,
            selectedRasi = selectedRasi,
            onSelectRasi = onRasiChange,
            onBack = onBack
        )
        MoreDestination.AI_PANDIT -> AiPanditSubScreen(
            language = language,
            messages = aiPanditMessages,
            isThinking = isPanditThinking,
            onSendMessage = onSendPanditMessage,
            onBack = onBack
        )
        MoreDestination.FESTIVALS -> FestivalsSubScreen(
            language = language,
            onBack = onBack
        )
        MoreDestination.GREETINGS -> GreetingsScreen(
            language = language,
            savedUserName = savedGreetingName,
            savedCreations = savedCreations,
            onSaveUserName = onSaveGreetingName,
            onSaveCreation = onSaveCreation,
            onDeleteCreation = onDeleteCreation,
            onBack = onBack,
            onShowMessage = onShowMessage
        )
        MoreDestination.SETTINGS -> SettingsSubScreen(
            language = language,
            selectedCity = selectedCity,
            selectedRasi = selectedRasi,
            themeMode = themeMode,
            notificationsEnabled = notificationsEnabled,
            morningNotifEnabled = morningNotifEnabled,
            morningNotifTime = morningNotifTime,
            rahuReminderEnabled = rahuReminderEnabled,
            festivalReminderEnabled = festivalReminderEnabled,
            onLanguageChange = onLanguageChange,
            onCityChange = onCityChange,
            onRasiChange = onRasiChange,
            onThemeModeChange = onThemeModeChange,
            onNotificationsToggle = onNotificationsToggle,
            onMorningNotifToggle = onMorningNotifToggle,
            onMorningTimeChange = onMorningTimeChange,
            onRahuReminderToggle = onRahuReminderToggle,
            onFestivalReminderToggle = onFestivalReminderToggle,
            onTestNotification = onTestNotification,
            onBack = onBack
        )
        MoreDestination.PREMIUM -> PremiumSubScreen(
            language = language,
            onBack = onBack
        )
        MoreDestination.NONE -> {
            // Main 7-Item Grid
            val menuItems = listOf(
                MoreGridItem(
                    destination = MoreDestination.RADIO,
                    translationKey = "menu_radio",
                    icon = Icons.Default.Radio,
                    color = TempleSaffron,
                    subtitleTe = "భక్తి సంకీర్తనలు & క్లాసిక్స్",
                    subtitleEn = "Devotional Chants & Classics",
                    testTag = "more_grid_radio"
                ),
                MoreGridItem(
                    destination = MoreDestination.CRICKET,
                    translationKey = "menu_cricket",
                    icon = Icons.Default.SportsCricket,
                    color = Color(0xFF1976D2),
                    subtitleTe = "లైవ్ స్కోర్లు & వ్యాఖ్యానం",
                    subtitleEn = "Live Scores & Updates",
                    testTag = "more_grid_cricket"
                ),
                MoreGridItem(
                    destination = MoreDestination.HOROSCOPE,
                    translationKey = "menu_horoscope",
                    icon = Icons.Default.Star,
                    color = TempleGold,
                    subtitleTe = "12 రాశుల నిత్య ఫలాలు",
                    subtitleEn = "12 Zodiac Predictions",
                    testTag = "more_grid_horoscope"
                ),
                MoreGridItem(
                    destination = MoreDestination.AI_PANDIT,
                    translationKey = "menu_ai_pandit",
                    icon = Icons.Default.Psychology,
                    color = TempleMaroon,
                    subtitleTe = "ధర్మ సందేహాల నివృత్తి",
                    subtitleEn = "Vedic Wisdom Assistant",
                    testTag = "more_grid_ai_pandit"
                ),
                MoreGridItem(
                    destination = MoreDestination.FESTIVALS,
                    translationKey = "menu_festivals",
                    icon = Icons.Default.Celebration,
                    color = Color(0xFFE91E63),
                    subtitleTe = "పండుగలు & ఏకాదశి వ్రతాలు",
                    subtitleEn = "Telugu Festivals Calendar",
                    testTag = "more_grid_festivals"
                ),
                MoreGridItem(
                    destination = MoreDestination.GREETINGS,
                    translationKey = "menu_greetings",
                    icon = Icons.Default.Celebration,
                    color = TempleSaffron,
                    subtitleTe = "శుభాకాంక్షల కార్డులు & AI",
                    subtitleEn = "Festive Greetings & AI",
                    testTag = "more_grid_greetings"
                ),
                MoreGridItem(
                    destination = MoreDestination.SETTINGS,
                    translationKey = "menu_settings",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF607D8B),
                    subtitleTe = "భాష, థీమ్, స్థానం",
                    subtitleEn = "Language, Theme, City",
                    testTag = "more_grid_settings"
                ),
                MoreGridItem(
                    destination = MoreDestination.PREMIUM,
                    translationKey = "menu_premium",
                    icon = Icons.Default.AutoAwesome,
                    color = TempleGold,
                    subtitleTe = "ప్రకటన రహిత గోల్డ్ విశేషాలు",
                    subtitleEn = "Pure Ad-Free Experience",
                    testTag = "more_grid_premium"
                )
            )

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .testTag("more_screen")
            ) {
                Text(
                    text = Translations.tr("more_title", language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(menuItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onNavigate(item.destination) }
                                .testTag(item.testTag),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (item.destination == MoreDestination.PREMIUM) TempleGold
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(item.color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = item.color,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = Translations.tr(item.translationKey, language),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (language == Language.TELUGU) item.subtitleTe else item.subtitleEn,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
