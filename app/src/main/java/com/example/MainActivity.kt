package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.components.BottomNavBar
import com.example.components.TopHeaderBar
import com.example.data.model.MoreDestination
import com.example.data.model.TabItem
import com.example.data.model.ThemeMode
import com.example.screens.HomeScreen
import com.example.screens.LiveTvScreen
import com.example.screens.MoreScreen
import com.example.screens.NewsScreen
import com.example.screens.OnboardingScreen
import com.example.screens.PanchangScreen
import com.example.ui.theme.ShubhodayaTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            val isDarkTheme = when (uiState.userPreferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            uiState.snackbarMessage?.let { msg ->
                LaunchedEffect(msg) {
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short
                    )
                    viewModel.dismissSnackbar()
                }
            }

            ShubhodayaTheme(darkTheme = isDarkTheme) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.widthIn(max = 600.dp)) {
                        if (!uiState.userPreferences.hasCompletedOnboarding) {
                            OnboardingScreen(
                                currentStep = uiState.onboardingStep,
                                selectedLanguage = uiState.userPreferences.language,
                                selectedCity = uiState.userPreferences.selectedCity,
                                selectedRasi = uiState.userPreferences.selectedRasi,
                                onLanguageChange = { viewModel.setLanguage(it) },
                                onCityChange = { viewModel.setCity(it) },
                                onRasiChange = { viewModel.setRasi(it) },
                                onStepChange = { viewModel.setOnboardingStep(it) },
                                onComplete = { viewModel.completeOnboarding() }
                            )
                        } else {
                            Scaffold(
                                topBar = {
                                    TopHeaderBar(
                                        selectedCity = uiState.userPreferences.selectedCity,
                                        currentLanguage = uiState.userPreferences.language,
                                        onToggleLanguage = { viewModel.toggleLanguage() },
                                        onCityClick = {
                                            viewModel.selectTab(TabItem.MORE)
                                            viewModel.navigateToMoreDestination(MoreDestination.SETTINGS)
                                        }
                                    )
                                },
                                bottomBar = {
                                    androidx.compose.foundation.layout.Column {
                                        // Persistent Audio Player Bar above bottom tabs
                                        com.example.components.BottomAudioPlayerBar(
                                            language = uiState.userPreferences.language,
                                            playerState = uiState.radioPlayerState,
                                            onTogglePlay = { viewModel.toggleRadioPlay() },
                                            onStop = { viewModel.stopRadio() },
                                            onClearError = { viewModel.clearRadioError() }
                                        )
                                        BottomNavBar(
                                            selectedTab = uiState.currentTab,
                                            currentLanguage = uiState.userPreferences.language,
                                            onTabSelected = { tab -> viewModel.selectTab(tab) }
                                        )
                                    }
                                },
                                snackbarHost = { SnackbarHost(snackbarHostState) },
                                modifier = Modifier.fillMaxSize()
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    when (uiState.currentTab) {
                                        TabItem.HOME -> HomeScreen(
                                            language = uiState.userPreferences.language,
                                            selectedCity = uiState.userPreferences.selectedCity,
                                            panchangData = uiState.panchangData,
                                            selectedRasi = uiState.userPreferences.selectedRasi,
                                            onNavigateTab = { tab -> viewModel.selectTab(tab) },
                                            onNavigateMore = { dest ->
                                                viewModel.selectTab(TabItem.MORE)
                                                viewModel.navigateToMoreDestination(dest)
                                            },
                                            onSelectChannel = { channel ->
                                                viewModel.selectLiveChannel(channel)
                                            },
                                            onOpenNewsDetail = { newsItem ->
                                                viewModel.openNewsDetail(newsItem)
                                            },
                                            onToggleBookmark = { newsId ->
                                                viewModel.toggleNewsBookmark(newsId)
                                            },
                                            bookmarkedIds = uiState.userPreferences.bookmarkedNewsIds,
                                            onOpenFestivalDetail = { festival ->
                                                viewModel.selectTab(TabItem.MORE)
                                                viewModel.navigateToMoreDestination(MoreDestination.FESTIVALS)
                                            },
                                            onSharePanchang = {
                                                viewModel.openSharePanchang(uiState.panchangDate)
                                            }
                                        )

                                        TabItem.PANCHANG -> PanchangScreen(
                                            language = uiState.userPreferences.language,
                                            selectedCity = uiState.userPreferences.selectedCity,
                                            panchangData = uiState.panchangData,
                                            selectedDate = uiState.panchangDate,
                                            onSelectDate = { date -> viewModel.selectPanchangDate(date) },
                                            onSharePanchang = { date -> viewModel.openSharePanchang(date) }
                                        )

                                        TabItem.LIVE_TV -> LiveTvScreen(
                                            language = uiState.userPreferences.language,
                                            selectedChannel = uiState.selectedChannel,
                                            selectedCategory = uiState.selectedLiveCategory,
                                            favoriteChannelIds = uiState.userPreferences.favoriteChannelIds,
                                            onSelectCategory = { cat -> viewModel.selectLiveCategory(cat) },
                                            onSelectChannel = { channel -> viewModel.selectLiveChannel(channel) },
                                            onToggleFavorite = { channelId -> viewModel.toggleFavoriteChannel(channelId) }
                                        )

                                        TabItem.NEWS -> NewsScreen(
                                            language = uiState.userPreferences.language,
                                            selectedCategory = uiState.selectedNewsCategory,
                                            articles = uiState.newsArticles,
                                            isLoading = uiState.isNewsLoading,
                                            lastUpdatedFormatted = uiState.newsLastUpdatedFormatted,
                                            bookmarkedIds = uiState.userPreferences.bookmarkedNewsIds,
                                            onSelectCategory = { cat -> viewModel.selectNewsCategory(cat) },
                                            onRefresh = { viewModel.refreshCurrentNews() },
                                            onToggleBookmark = { id -> viewModel.toggleNewsBookmark(id) },
                                            onShareNews = { title, link ->
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "$title\n$link\n- via Shubhodaya")
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, null)
                                                startActivity(shareIntent)
                                            }
                                        )

                                        TabItem.MORE -> MoreScreen(
                                            currentDestination = uiState.moreDestination,
                                            language = uiState.userPreferences.language,
                                            selectedCity = uiState.userPreferences.selectedCity,
                                            selectedRasi = uiState.userPreferences.selectedRasi,
                                            themeMode = uiState.userPreferences.themeMode,
                                            notificationsEnabled = uiState.userPreferences.notificationsEnabled,
                                            selectedRadioCategory = uiState.selectedRadioCategory,
                                            radioStations = uiState.radioStations,
                                            isRadioLoadingStations = uiState.isRadioLoadingStations,
                                            radioPlayerState = uiState.radioPlayerState,
                                            cricketMatches = uiState.cricketMatches,
                                            isCricketLoading = uiState.isCricketLoading,
                                            cricketMinutesAgo = uiState.cricketMinutesAgo,
                                            cricketMessage = uiState.cricketMessage,
                                            onRefreshCricket = { viewModel.loadCricketMatches(forceRefresh = true) },
                                            aiPanditMessages = uiState.aiPanditMessages,
                                            isPanditThinking = uiState.isPanditThinking,
                                            onNavigate = { dest -> viewModel.navigateToMoreDestination(dest) },
                                            onBack = { viewModel.navigateBackFromMore() },
                                            onLanguageChange = { viewModel.setLanguage(it) },
                                            onCityChange = { viewModel.setCity(it) },
                                            onRasiChange = { viewModel.setRasi(it) },
                                            onThemeModeChange = { viewModel.setThemeMode(it) },
                                            onNotificationsToggle = { viewModel.setNotificationsEnabled(it) },
                                            morningNotifEnabled = uiState.userPreferences.morningNotificationEnabled,
                                            morningNotifTime = uiState.userPreferences.morningNotificationTime,
                                            rahuReminderEnabled = uiState.userPreferences.rahuKalamReminderEnabled,
                                            festivalReminderEnabled = uiState.userPreferences.festivalReminderEnabled,
                                            onMorningNotifToggle = { viewModel.setMorningNotificationEnabled(it) },
                                            onMorningTimeChange = { viewModel.setMorningNotificationTime(it) },
                                            onRahuReminderToggle = { viewModel.setRahuKalamReminderEnabled(it) },
                                            onFestivalReminderToggle = { viewModel.setFestivalReminderEnabled(it) },
                                            onTestNotification = { viewModel.triggerTestNotification(it) },
                                            onSelectRadioCategory = { viewModel.selectRadioCategory(it) },
                                            onSelectRadioStation = { viewModel.playRadioStation(it) },
                                            onToggleRadioPlay = { viewModel.toggleRadioPlay() },
                                            onSendPanditMessage = { viewModel.askAiPandit(it) },
                                            savedGreetingName = uiState.userPreferences.lastGreetingName,
                                            savedCreations = uiState.userPreferences.savedCreations,
                                            onSaveGreetingName = { viewModel.saveGreetingName(it) },
                                            onSaveCreation = { viewModel.addSavedCreation(it) },
                                            onDeleteCreation = { viewModel.deleteSavedCreation(it) },
                                            onShowMessage = { viewModel.showMessage(it) }
                                        )
                                    }

                                    if (uiState.isShareSheetVisible) {
                                        com.example.components.PanchangSharePreviewBottomSheet(
                                            panchang = uiState.panchangData,
                                            targetDate = uiState.shareTargetDate,
                                            language = uiState.userPreferences.language,
                                            cardsSharedCount = uiState.userPreferences.cardsShared,
                                            onDismiss = { viewModel.closeSharePanchang() },
                                            onCardShared = { viewModel.onCardShared() }
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
}
