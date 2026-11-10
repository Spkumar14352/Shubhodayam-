package com.example.data.model

enum class Language(val code: String, val nativeName: String, val englishName: String) {
    TELUGU("te", "తెలుగు", "Telugu"),
    ENGLISH("en", "English", "English")
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class TabItem {
    HOME,
    PANCHANG,
    LIVE_TV,
    NEWS,
    MORE
}

enum class MoreDestination {
    NONE,
    RADIO,
    CRICKET,
    HOROSCOPE,
    AI_PANDIT,
    FESTIVALS,
    GREETINGS,
    BABY_NAMES,
    GRAHANAM,
    MUHURTHAMS,
    SHUBHALEKHA,
    SETTINGS,
    PREMIUM
}
