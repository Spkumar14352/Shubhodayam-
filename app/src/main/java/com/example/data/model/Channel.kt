package com.example.data.model

enum class ChannelCategory(val key: String, val titleTe: String, val titleEn: String) {
    FAVORITES("Favorites", "ఇష్టమైనవి", "Favorites"),
    ALL("All", "అన్నీ", "All"),
    DEVOTIONAL("Devotional", "భక్తి & ఆధ్యాత్మికం", "Devotional"),
    TELUGU_NEWS("Telugu News", "తెలుగు వార్తలు", "Telugu News"),
    NATIONAL_NEWS("National News", "జాతీయ వార్తలు", "National News"),
    ENTERTAINMENT("Entertainment", "వినోదం", "Entertainment")
}

data class Channel(
    val id: String,
    val name: String,
    val nameTelugu: String,
    val category: String,
    val youtubeChannelId: String,
    val logoUrl: String = ""
) {
    fun getInitials(): String {
        val words = name.trim().split(" ")
        return when {
            words.size >= 2 -> "${words[0].take(1)}${words[1].take(1)}".uppercase()
            name.length >= 2 -> name.take(2).uppercase()
            else -> name.uppercase()
        }
    }
}

// TODO: add official YouTube channel IDs
// Fill in official YouTube Channel IDs (e.g., "UCxxxxxx...") for live stream embeds.
object ChannelsData {
    val allChannels = listOf(
        // --- Devotional ---
        Channel(
            id = "bhakthi_tv",
            name = "Bhakthi TV",
            nameTelugu = "భక్తి టీవీ",
            category = "Devotional",
            youtubeChannelId = "UCSzOZ97LOpU-_AVlGfmD4rQ",
            logoUrl = ""
        ),
        Channel(
            id = "svbc_ttd",
            name = "SVBC TTD Live",
            nameTelugu = "ఎస్వీబీసీ (తిరుమల లైవ్)",
            category = "Devotional",
            youtubeChannelId = "UCS2Y83GD-fc7qqgNW5uj41g",
            logoUrl = ""
        ),
        Channel(
            id = "dd_yadagiri",
            name = "DD Yadagiri Devotional",
            nameTelugu = "డీడీ యాదగిరి",
            category = "Devotional",
            youtubeChannelId = "", // TODO: add official YouTube channel ID for DD Yadagiri
            logoUrl = ""
        ),
        Channel(
            id = "hindu_dharmam",
            name = "Hindu Dharmam TV",
            nameTelugu = "హిందూ ధర్మం టీవీ",
            category = "Devotional",
            youtubeChannelId = "", // TODO: add official YouTube channel ID for Hindu Dharmam TV
            logoUrl = ""
        ),

        // --- Telugu News ---
        Channel(
            id = "tv9_telugu",
            name = "TV9 Telugu",
            nameTelugu = "టీవీ9 తెలుగు",
            category = "Telugu News",
            youtubeChannelId = "UCPXTXMecYqnRKNdqdVOGSFg",
            logoUrl = ""
        ),
        Channel(
            id = "ntv_telugu",
            name = "NTV Telugu",
            nameTelugu = "ఎన్‌టీవీ తెలుగు",
            category = "Telugu News",
            youtubeChannelId = "UCumtYpCY26F6Jr3satUgMvA",
            logoUrl = ""
        ),
        Channel(
            id = "abn_andhrajyothy",
            name = "ABN Andhra Jyothi",
            nameTelugu = "ఏబీఎన్ ఆంధ్రజ్యోతి",
            category = "Telugu News",
            youtubeChannelId = "UC_2irx_BQR7RsBKmUV9fePQ",
            logoUrl = ""
        ),
        Channel(
            id = "v6_news",
            name = "V6 News",
            nameTelugu = "వి6 న్యూస్",
            category = "Telugu News",
            youtubeChannelId = "UCDCMjD1XIAsCZsYHNMGVcog",
            logoUrl = ""
        ),
        Channel(
            id = "sakshi_tv",
            name = "Sakshi TV",
            nameTelugu = "సాక్షి టీవీ",
            category = "Telugu News",
            youtubeChannelId = "UCQ_FATLW83q-4xJ2fsi8qAw",
            logoUrl = ""
        ),

        // --- National News ---
        Channel(
            id = "dd_india",
            name = "DD India Live",
            nameTelugu = "డీడీ ఇండియా",
            category = "National News",
            youtubeChannelId = "UCGDQNvybfDDeGTf4GtigXaw",
            logoUrl = ""
        ),
        Channel(
            id = "sansad_tv",
            name = "Sansad TV",
            nameTelugu = "సంసద్ టీవీ",
            category = "National News",
            youtubeChannelId = "UCISgnSNwqQ2i8lhCun3KtQg",
            logoUrl = ""
        ),
        Channel(
            id = "ndtv_india",
            name = "NDTV India",
            nameTelugu = "ఎన్డీటీవీ ఇండియా",
            category = "National News",
            youtubeChannelId = "UC9CYT9gSNLevX5ey2_6CK0Q",
            logoUrl = ""
        ),

        // --- Entertainment ---
        Channel(
            id = "etv_cinema",
            name = "ETV Cinema",
            nameTelugu = "ఈటీవీ సినిమా",
            category = "Entertainment",
            youtubeChannelId = "UC3Dvbf4eEov7YORy5nEioTA",
            logoUrl = ""
        ),
        Channel(
            id = "gemini_comedy",
            name = "Gemini Comedy",
            nameTelugu = "జెమిని కామెడీ",
            category = "Entertainment",
            youtubeChannelId = "", // TODO: add official YouTube channel ID for Gemini Comedy
            logoUrl = ""
        ),
        Channel(
            id = "maa_gold",
            name = "Star Maa Gold",
            nameTelugu = "స్టార్ మా గోల్డ్",
            category = "Entertainment",
            youtubeChannelId = "", // TODO: add official YouTube channel ID for Star Maa Gold
            logoUrl = ""
        )
    )

    fun getById(id: String): Channel = allChannels.find { it.id == id } ?: allChannels.first()
}
