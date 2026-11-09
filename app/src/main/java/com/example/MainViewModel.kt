package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Channel
import com.example.data.model.ChannelsData
import com.example.data.model.City
import com.example.data.model.CityData
import com.example.data.model.Festival
import com.example.data.model.Language
import com.example.data.model.MediaSampleData
import com.example.data.model.MoreDestination
import com.example.data.model.NewsItem
import com.example.data.model.PanchangData
import com.example.data.model.PanchangProvider
import com.example.data.model.RadioBrowserStation
import com.example.data.model.Rasi
import com.example.data.model.RasiData
import com.example.data.model.TabItem
import com.example.data.model.ThemeMode
import com.example.data.repository.RadioRepository
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import com.example.services.RadioPlayerManager
import com.example.services.RadioPlayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AiPanditMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: String = "ఇప్పుడే"
)

data class MainUiState(
    val userPreferences: UserPreferences = UserPreferences(),
    val currentTab: TabItem = TabItem.HOME,
    val moreDestination: MoreDestination = MoreDestination.NONE,
    val onboardingStep: Int = 1,
    val panchangDate: LocalDate = LocalDate.now(),
    val panchangData: PanchangData = PanchangProvider.getPanchangForDate(LocalDate.now(), CityData.defaultCity),
    val selectedChannel: Channel = ChannelsData.allChannels.first(),
    val selectedLiveCategory: String = "ALL", // "FAVORITES", "ALL", "Devotional", "Telugu News", "National News", "Entertainment"
    val radioPlayerState: RadioPlayerState = RadioPlayerState(),
    val selectedRadioCategory: String = "TELUGU", // "TELUGU", "DEVOTIONAL", "NATIONAL"
    val radioStations: List<RadioBrowserStation> = emptyList(),
    val isRadioLoadingStations: Boolean = false,
    val selectedNewsCategory: String = "Telugu", // "Telugu", "India", "Sports", "Devotional", "BOOKMARKS"
    val newsArticles: List<com.example.data.model.RssArticle> = emptyList(),
    val isNewsLoading: Boolean = false,
    val newsLastUpdatedFormatted: String = "",
    val cricketMatches: List<com.example.data.model.LiveCricketMatch> = emptyList(),
    val isCricketLoading: Boolean = false,
    val cricketMinutesAgo: Long = 0,
    val cricketMessage: String? = null,
    val readingArticle: com.example.data.model.RssArticle? = null,
    val readingNewsItem: NewsItem? = null,
    val selectedFestival: Festival? = null,
    val aiPanditMessages: List<AiPanditMessage> = emptyList(),
    val isPanditThinking: Boolean = false,
    val snackbarMessage: String? = null,
    val isShareSheetVisible: Boolean = false,
    val shareTargetDate: LocalDate = LocalDate.now()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)
    private val radioRepository = RadioRepository()
    private val radioPlayerManager = RadioPlayerManager(application)
    private val newsRepository = com.example.data.repository.NewsRepository(application)
    private val cricketRepository = com.example.data.repository.CricketRepository(application)
    private val notificationScheduler = com.example.services.LocalNotificationScheduler(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Collect user preferences
        viewModelScope.launch {
            repository.userPreferences.collect { prefs ->
                val panchang = PanchangProvider.getPanchangForDate(_uiState.value.panchangDate, prefs.selectedCity)
                _uiState.value = _uiState.value.copy(
                    userPreferences = prefs,
                    panchangData = panchang
                )
            }
        }

        // Collect radio player state
        viewModelScope.launch {
            radioPlayerManager.playerState.collect { pState ->
                _uiState.value = _uiState.value.copy(radioPlayerState = pState)
            }
        }

        // Load initial radio stations
        loadRadioStations("TELUGU")

        // Load initial news and cricket matches
        loadNews("Telugu", forceRefresh = false)
        loadCricketMatches(forceRefresh = false)

        initializeAiPandit()
    }

    private fun initializeAiPandit() {
        val initialMessageTe = "నమస్కారం! నేను మీ శుభోదయ AI పండిట్‌ని. శుభ ముహూర్తాలు, పూజా విధానాలు, వ్రతాలు, తిథి నియమాలు లేదా మీ రాశిఫలాల గురించి ఏ సందేహం ఉన్నా నన్ను అడగవచ్చు. మీకు ఎలా సహాయపడగలను?"
        val initialMessageEn = "Namaste! I am your Shubhodaya AI Pandit. Feel free to ask about auspicious Muhurthams, puja procedures, fasting rules, or astrological insights. How may I assist you today?"

        val isTelugu = _uiState.value.userPreferences.language == Language.TELUGU
        _uiState.value = _uiState.value.copy(
            aiPanditMessages = listOf(
                AiPanditMessage(
                    isUser = false,
                    text = if (isTelugu) initialMessageTe else initialMessageEn,
                    timestamp = "06:00 AM"
                )
            )
        )
    }

    fun setLanguage(language: Language) {
        repository.setLanguage(language)
    }

    fun toggleLanguage() {
        val next = if (_uiState.value.userPreferences.language == Language.TELUGU) Language.ENGLISH else Language.TELUGU
        repository.setLanguage(next)
    }

    fun setCity(city: City) {
        repository.setCity(city)
        val panchang = PanchangProvider.getPanchangForDate(_uiState.value.panchangDate, city)
        _uiState.value = _uiState.value.copy(panchangData = panchang)
    }

    fun setRasi(rasi: Rasi) {
        repository.setRasi(rasi)
    }

    fun setThemeMode(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }

    fun setMorningNotificationEnabled(enabled: Boolean) {
        repository.setMorningNotificationEnabled(enabled)
    }

    fun setMorningNotificationTime(time: String) {
        repository.setMorningNotificationTime(time)
    }

    fun setRahuKalamReminderEnabled(enabled: Boolean) {
        repository.setRahuKalamReminderEnabled(enabled)
    }

    fun setFestivalReminderEnabled(enabled: Boolean) {
        repository.setFestivalReminderEnabled(enabled)
    }

    fun triggerTestNotification(type: String) {
        notificationScheduler.triggerTestNotification(
            type = type,
            prefs = _uiState.value.userPreferences,
            panchang = _uiState.value.panchangData
        )
    }

    fun selectTab(tab: TabItem) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            moreDestination = MoreDestination.NONE
        )
    }

    fun navigateToMoreDestination(destination: MoreDestination) {
        _uiState.value = _uiState.value.copy(moreDestination = destination)
    }

    fun navigateBackFromMore() {
        _uiState.value = _uiState.value.copy(moreDestination = MoreDestination.NONE)
    }

    fun setOnboardingStep(step: Int) {
        _uiState.value = _uiState.value.copy(onboardingStep = step)
    }

    fun completeOnboarding() {
        repository.setHasCompletedOnboarding(true)
    }

    fun selectPanchangDate(date: LocalDate) {
        val panchang = PanchangProvider.getPanchangForDate(date, _uiState.value.userPreferences.selectedCity)
        _uiState.value = _uiState.value.copy(
            panchangDate = date,
            panchangData = panchang
        )
    }

    // --- Live TV Methods ---
    fun selectLiveCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedLiveCategory = category)
    }

    fun selectLiveChannel(channel: Channel) {
        _uiState.value = _uiState.value.copy(selectedChannel = channel)
    }

    fun toggleFavoriteChannel(channelId: String) {
        repository.toggleFavoriteChannel(channelId)
    }

    // --- Radio Browser API & Player Methods ---
    fun selectRadioCategory(categoryKey: String) {
        _uiState.value = _uiState.value.copy(selectedRadioCategory = categoryKey)
        loadRadioStations(categoryKey)
    }

    private fun loadRadioStations(categoryKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRadioLoadingStations = true)
            val stations = radioRepository.getStations(categoryKey)
            _uiState.value = _uiState.value.copy(
                radioStations = stations,
                isRadioLoadingStations = false
            )
        }
    }

    fun playRadioStation(station: RadioBrowserStation) {
        radioPlayerManager.playStation(station)
    }

    fun toggleRadioPlay() {
        radioPlayerManager.togglePlay()
    }

    fun stopRadio() {
        radioPlayerManager.stop()
    }

    fun clearRadioError() {
        radioPlayerManager.clearError()
    }

    // --- Real RSS News via rss2json ---
    fun selectNewsCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedNewsCategory = category)
        if (category != "BOOKMARKS") {
            loadNews(category, forceRefresh = false)
        }
    }

    fun refreshCurrentNews() {
        val cat = _uiState.value.selectedNewsCategory
        if (cat != "BOOKMARKS") {
            loadNews(cat, forceRefresh = true)
        }
    }

    fun loadNews(category: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isNewsLoading = true)
            val articles = newsRepository.getNewsForCategory(category, forceRefresh = forceRefresh)
            val lastUpdated = newsRepository.getLastUpdatedTime(category)
            val formattedTime = if (lastUpdated > 0) {
                java.text.SimpleDateFormat("hh:mm a, dd MMM", java.util.Locale.getDefault()).format(java.util.Date(lastUpdated))
            } else {
                "Just now"
            }
            _uiState.value = _uiState.value.copy(
                newsArticles = articles,
                isNewsLoading = false,
                newsLastUpdatedFormatted = formattedTime
            )
        }
    }

    fun openArticle(article: com.example.data.model.RssArticle) {
        _uiState.value = _uiState.value.copy(readingArticle = article)
    }

    fun closeArticle() {
        _uiState.value = _uiState.value.copy(readingArticle = null)
    }

    // --- Cricket via CricketData.org (5-minute cache) ---
    fun loadCricketMatches(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCricketLoading = true)
            when (val result = cricketRepository.getCurrentMatches(forceRefresh = forceRefresh)) {
                is com.example.data.repository.CricketResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        cricketMatches = result.matches,
                        cricketMinutesAgo = result.lastUpdatedMinutesAgo,
                        isCricketLoading = false,
                        cricketMessage = null
                    )
                }
                is com.example.data.repository.CricketResult.MissingApiKey -> {
                    _uiState.value = _uiState.value.copy(
                        cricketMatches = com.example.data.model.CricketSampleData.sampleMatches,
                        cricketMinutesAgo = 0,
                        isCricketLoading = false,
                        cricketMessage = result.message
                    )
                }
                is com.example.data.repository.CricketResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        cricketMatches = result.fallbackMatches,
                        cricketMinutesAgo = 0,
                        isCricketLoading = false,
                        cricketMessage = result.message
                    )
                }
            }
        }
    }

    fun openNewsDetail(item: NewsItem) {
        _uiState.value = _uiState.value.copy(readingNewsItem = item)
    }

    fun closeNewsDetail() {
        _uiState.value = _uiState.value.copy(readingNewsItem = null)
    }

    fun toggleNewsBookmark(newsId: String) {
        repository.toggleBookmark(newsId)
    }

    fun openFestivalDetail(festival: Festival) {
        _uiState.value = _uiState.value.copy(selectedFestival = festival)
    }

    fun closeFestivalDetail() {
        _uiState.value = _uiState.value.copy(selectedFestival = null)
    }

    fun openSharePanchang(date: LocalDate = _uiState.value.panchangDate) {
        val targetPanchang = PanchangProvider.getPanchangForDate(date, _uiState.value.userPreferences.selectedCity)
        _uiState.value = _uiState.value.copy(
            isShareSheetVisible = true,
            shareTargetDate = date,
            panchangData = targetPanchang
        )
    }

    fun closeSharePanchang() {
        _uiState.value = _uiState.value.copy(isShareSheetVisible = false)
    }

    fun onCardShared() {
        repository.incrementCardsShared()
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun showMessage(msg: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = msg)
    }

    fun saveGreetingName(name: String) {
        repository.saveGreetingName(name)
    }

    fun addSavedCreation(creation: com.example.data.model.SavedGreeting) {
        repository.addSavedCreation(creation)
    }

    fun deleteSavedCreation(id: String) {
        repository.deleteSavedCreation(id)
    }

    fun askAiPandit(question: String) {
        if (question.isBlank()) return

        val userMsg = AiPanditMessage(isUser = true, text = question.trim())
        val updatedList = _uiState.value.aiPanditMessages + userMsg
        _uiState.value = _uiState.value.copy(
            aiPanditMessages = updatedList,
            isPanditThinking = true
        )

        viewModelScope.launch {
            delay(1200)
            val isTelugu = _uiState.value.userPreferences.language == Language.TELUGU
            val answer = generatePanditAnswer(question, isTelugu, _uiState.value.userPreferences.selectedRasi)
            val panditMsg = AiPanditMessage(isUser = false, text = answer)
            _uiState.value = _uiState.value.copy(
                aiPanditMessages = _uiState.value.aiPanditMessages + panditMsg,
                isPanditThinking = false
            )
        }
    }

    private fun generatePanditAnswer(query: String, isTe: Boolean, userRasi: Rasi): String {
        val q = query.lowercase()
        return if (isTe) {
            when {
                q.contains("ముహూర్తం") || q.contains("మంచి సమయం") ->
                    "నేటి అభిజిత్ ముహూర్తం ఉదయం 11:45 నుండి మధ్యాహ్నం 12:32 వరకు అత్యంత శ్రేష్టం. ఈ సమయంలో చేపట్టిన నూతన పనులు, ప్రయాణాలు మరియు ఒప్పందాలు నిర్విఘ్నంగా పూర్తవుతాయి. రాహుకాలం సమయంలో ముఖ్యమైన కార్యాలను నివారించండి."
                q.contains("ఉపవాసం") || q.contains("ఏకాదశి") ->
                    "ఏకాదశి ఉపవాస నియమాలు: దశమి నాటి రాత్రి నుండే అల్పాహారం తీసుకోవాలి. ఏకాదశి నాడు ఉదయం సూర్యోదయానికి ముందే స్నానమాచరించి విష్ణుమూర్తిని తులసీదళాలతో పూజించాలి. రోజంతా దైవ నామస్మరణతో గడపాలి. ద్వాదశి నాడు సూర్యోదయం తర్వాత పారణ (భోజనం) చేసి ఉపవాస దీక్ష ముగించాలి."
                q.contains("రాశి") || q.contains(userRasi.nameTe) ->
                    "${userRasi.nameTe} రాశి వారికి ఈ వారం అనుకూలంగా ఉంది. మీ అధిపతి ${userRasi.rulingPlanetTe} అనుగ్రహంతో నిలిచిపోయిన పనులు సానుకూలమవుతాయి. అదృష్ట సంఖ్య: ${userRasi.luckyNumber}, అనుకూల రంగు: ${userRasi.luckyColorTe}. నిత్యం ఇష్టదేవతారాధన మరియు దీపారాధన శ్రేయస్కరం."
                else ->
                    "మీ ధర్మ సందేహం శ్లాఘనీయం. వేద వాఙ్మయం ప్రకారం, ఏ సత్కార్యానికైనా ప్రశాంతమైన మనస్సు, ధర్మవర్తన మరియు సద్గుణములు ముఖ్యం. మీ నిత్య దినచర్యలో గాయత్రీ మంత్రం లేదా శివ/విష్ణు నామస్మరణ చేయండి. మీకు సదా శుభం కలుగుగాక! ఓం శాంతిః శాంతిః శాంతిః."
            }
        } else {
            when {
                q.contains("muhurtham") || q.contains("time") || q.contains("good") ->
                    "Today's Abhijit Muhurtham from 11:45 AM to 12:32 PM is highly auspicious for new initiatives, auspicious agreements, and travel. Kindly avoid Rahu Kalam for starting critical tasks."
                q.contains("fast") || q.contains("ekadasi") || q.contains("vrat") ->
                    "Ekadashi fasting guidelines: Consume light meals from Dashami night. On Ekadashi, take a holy morning bath before sunrise, worship Lord Vishnu with sacred Tulasi leaves, maintain mindful contemplation, and conclude the fast on Dwadashi during Parana hours."
                else ->
                    "According to Vedic philosophy, pure devotion, righteousness, and peaceful perseverance bring auspicious harmony to every aspect of life. Chanting the sacred Gayatri mantra or your Ishta Devata slokas each morning will bring calm focus. May you be blessed with peace and prosperity."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        radioPlayerManager.stop()
    }
}
