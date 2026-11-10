package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.City
import com.example.data.model.CityData
import com.example.data.model.Language
import com.example.data.model.Rasi
import com.example.data.model.RasiData
import com.example.data.model.ShortlistedName
import com.example.data.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferences(
    val language: Language = Language.TELUGU,
    val selectedCity: City = CityData.defaultCity,
    val selectedRasi: Rasi = RasiData.allRasis.first(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hasCompletedOnboarding: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val morningNotificationEnabled: Boolean = true,
    val morningNotificationTime: String = "06:30", // HH:mm
    val rahuKalamReminderEnabled: Boolean = true,
    val festivalReminderEnabled: Boolean = true,
    val bookmarkedNewsIds: Set<String> = emptySet(),
    val favoriteChannelIds: Set<String> = emptySet(),
    val cardsShared: Int = 0,
    val lastGreetingName: String = "",
    val savedCreations: List<com.example.data.model.SavedGreeting> = emptyList(),
    val babyNameShortlist: List<ShortlistedName> = emptyList()
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shubhodaya_user_prefs", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val langCode = prefs.getString("language_code", Language.TELUGU.code) ?: Language.TELUGU.code
        val language = if (langCode == "en") Language.ENGLISH else Language.TELUGU

        val cityId = prefs.getString("city_id", CityData.defaultCity.id) ?: CityData.defaultCity.id
        val city = CityData.cities.find { it.id == cityId } ?: CityData.defaultCity

        val rasiId = prefs.getString("rasi_id", RasiData.allRasis.first().id) ?: RasiData.allRasis.first().id
        val rasi = RasiData.getById(rasiId)

        val themeStr = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeStr)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }

        val completedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        val notifications = prefs.getBoolean("notifications_enabled", true)
        val morningEnabled = prefs.getBoolean("morning_notification_enabled", true)
        val morningTime = prefs.getString("morning_notification_time", "06:30") ?: "06:30"
        val rahuReminder = prefs.getBoolean("rahu_kalam_reminder_enabled", true)
        val festivalReminder = prefs.getBoolean("festival_reminder_enabled", true)
        val bookmarks = prefs.getStringSet("bookmarked_news", emptySet()) ?: emptySet()
        val favorites = prefs.getStringSet("favorite_channels", emptySet()) ?: emptySet()
        val cardsShared = prefs.getInt("cards_shared", 0)
        val lastGreetingName = prefs.getString("last_greeting_name", "") ?: ""
        val savedCreations = loadSavedCreations()

        return UserPreferences(
            language = language,
            selectedCity = city,
            selectedRasi = rasi,
            themeMode = themeMode,
            hasCompletedOnboarding = completedOnboarding,
            notificationsEnabled = notifications,
            morningNotificationEnabled = morningEnabled,
            morningNotificationTime = morningTime,
            rahuKalamReminderEnabled = rahuReminder,
            festivalReminderEnabled = festivalReminder,
            bookmarkedNewsIds = bookmarks,
            favoriteChannelIds = favorites,
            cardsShared = cardsShared,
            lastGreetingName = lastGreetingName,
            savedCreations = savedCreations,
            babyNameShortlist = loadBabyNameShortlist()
        )
    }

    private fun loadBabyNameShortlist(): List<ShortlistedName> {
        val raw = prefs.getString("baby_name_shortlist_json", "[]") ?: "[]"
        return try {
            val array = org.json.JSONArray(raw)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ShortlistedName(
                    name = obj.getString("name"),
                    telugu = obj.getString("telugu"),
                    meaningEn = obj.optString("meaningEn"),
                    meaningTe = obj.optString("meaningTe"),
                    isAiSuggested = obj.optBoolean("isAiSuggested")
                )
            }
        } catch (e: org.json.JSONException) {
            emptyList()
        }
    }

    fun toggleBabyNameShortlist(item: ShortlistedName) {
        val current = _userPreferences.value.babyNameShortlist
        val isSaved = current.any { it.name.equals(item.name, ignoreCase = true) }
        val updated = if (isSaved) {
            current.filterNot { it.name.equals(item.name, ignoreCase = true) }
        } else {
            current + item
        }

        val array = org.json.JSONArray()
        updated.forEach {
            array.put(
                org.json.JSONObject()
                    .put("name", it.name)
                    .put("telugu", it.telugu)
                    .put("meaningEn", it.meaningEn)
                    .put("meaningTe", it.meaningTe)
                    .put("isAiSuggested", it.isAiSuggested)
            )
        }
        prefs.edit().putString("baby_name_shortlist_json", array.toString()).apply()
        _userPreferences.value = _userPreferences.value.copy(babyNameShortlist = updated)
    }

    private fun loadSavedCreations(): List<com.example.data.model.SavedGreeting> {
        val raw = prefs.getString("saved_creations_json", "[]") ?: "[]"
        return try {
            val jsonArray = org.json.JSONArray(raw)
            val list = mutableListOf<com.example.data.model.SavedGreeting>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    com.example.data.model.SavedGreeting(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        filePath = obj.optString("filePath", ""),
                        templateTitle = obj.optString("templateTitle", ""),
                        userName = obj.optString("userName", ""),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveGreetingName(name: String) {
        prefs.edit().putString("last_greeting_name", name).apply()
        _userPreferences.value = _userPreferences.value.copy(lastGreetingName = name)
    }

    fun addSavedCreation(creation: com.example.data.model.SavedGreeting) {
        val current = _userPreferences.value.savedCreations.toMutableList()
        current.removeAll { it.id == creation.id }
        current.add(0, creation)
        val trimmed = if (current.size > 20) current.take(20) else current

        val jsonArray = org.json.JSONArray()
        for (item in trimmed) {
            val obj = org.json.JSONObject()
            obj.put("id", item.id)
            obj.put("filePath", item.filePath)
            obj.put("templateTitle", item.templateTitle)
            obj.put("userName", item.userName)
            obj.put("timestamp", item.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_creations_json", jsonArray.toString()).apply()
        _userPreferences.value = _userPreferences.value.copy(savedCreations = trimmed)
    }

    fun deleteSavedCreation(id: String) {
        val current = _userPreferences.value.savedCreations.filter { it.id != id }
        val jsonArray = org.json.JSONArray()
        for (item in current) {
            val obj = org.json.JSONObject()
            obj.put("id", item.id)
            obj.put("filePath", item.filePath)
            obj.put("templateTitle", item.templateTitle)
            obj.put("userName", item.userName)
            obj.put("timestamp", item.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_creations_json", jsonArray.toString()).apply()
        _userPreferences.value = _userPreferences.value.copy(savedCreations = current)
    }

    fun setLanguage(language: Language) {
        prefs.edit().putString("language_code", language.code).apply()
        _userPreferences.value = _userPreferences.value.copy(language = language)
    }

    fun setCity(city: City) {
        prefs.edit().putString("city_id", city.id).apply()
        _userPreferences.value = _userPreferences.value.copy(selectedCity = city)
    }

    fun setRasi(rasi: Rasi) {
        prefs.edit().putString("rasi_id", rasi.id).apply()
        _userPreferences.value = _userPreferences.value.copy(selectedRasi = rasi)
    }

    fun setThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString("theme_mode", themeMode.name).apply()
        _userPreferences.value = _userPreferences.value.copy(themeMode = themeMode)
    }

    fun setHasCompletedOnboarding(completed: Boolean) {
        prefs.edit().putBoolean("has_completed_onboarding", completed).apply()
        _userPreferences.value = _userPreferences.value.copy(hasCompletedOnboarding = completed)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(notificationsEnabled = enabled)
    }

    fun setMorningNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("morning_notification_enabled", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(morningNotificationEnabled = enabled)
    }

    fun setMorningNotificationTime(time: String) {
        prefs.edit().putString("morning_notification_time", time).apply()
        _userPreferences.value = _userPreferences.value.copy(morningNotificationTime = time)
    }

    fun setRahuKalamReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("rahu_kalam_reminder_enabled", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(rahuKalamReminderEnabled = enabled)
    }

    fun setFestivalReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("festival_reminder_enabled", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(festivalReminderEnabled = enabled)
    }

    fun toggleBookmark(newsId: String) {
        val current = _userPreferences.value.bookmarkedNewsIds.toMutableSet()
        if (current.contains(newsId)) {
            current.remove(newsId)
        } else {
            current.add(newsId)
        }
        prefs.edit().putStringSet("bookmarked_news", current).apply()
        _userPreferences.value = _userPreferences.value.copy(bookmarkedNewsIds = current)
    }

    fun toggleFavoriteChannel(channelId: String) {
        val current = _userPreferences.value.favoriteChannelIds.toMutableSet()
        if (current.contains(channelId)) {
            current.remove(channelId)
        } else {
            current.add(channelId)
        }
        prefs.edit().putStringSet("favorite_channels", current).apply()
        _userPreferences.value = _userPreferences.value.copy(favoriteChannelIds = current)
    }

    fun incrementCardsShared(): Int {
        val next = _userPreferences.value.cardsShared + 1
        prefs.edit().putInt("cards_shared", next).apply()
        _userPreferences.value = _userPreferences.value.copy(cardsShared = next)
        return next
    }
}
