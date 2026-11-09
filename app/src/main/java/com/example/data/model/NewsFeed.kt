package com.example.data.model

data class NewsFeed(
    val id: String,
    val name: String,
    val nameTelugu: String,
    val category: String, // "Telugu", "India", "Sports", "Devotional"
    val feedUrl: String
)

object NewsFeedsData {
    val feeds = listOf(
        // Telugu
        // TODO: verify feeds
        NewsFeed(
            id = "eenadu_ap",
            name = "Eenadu",
            nameTelugu = "ఈనాడు",
            category = "Telugu",
            feedUrl = "https://www.eenadu.net/rss/andhra-pradesh.xml"
        ),
        NewsFeed(
            id = "sakshi_ts",
            name = "Sakshi",
            nameTelugu = "సాక్షి",
            category = "Telugu",
            feedUrl = "https://www.sakshi.com/rss/telangana.xml"
        ),
        NewsFeed(
            id = "andhrajyothy_news",
            name = "Andhra Jyothy",
            nameTelugu = "ఆంధ్రజ్యోతి",
            category = "Telugu",
            feedUrl = "https://www.andhrajyothy.com/rss/ap-telangana.xml"
        ),

        // India / National
        // TODO: verify feeds
        NewsFeed(
            id = "hindu_national",
            name = "The Hindu",
            nameTelugu = "ది హిందూ",
            category = "India",
            feedUrl = "https://www.thehindu.com/news/national/feeder/default.rss"
        ),
        NewsFeed(
            id = "ndtv_india",
            name = "NDTV India",
            nameTelugu = "ఎన్డీటీవీ",
            category = "India",
            feedUrl = "https://feeds.feedburner.com/ndtvnews-india-news"
        ),
        NewsFeed(
            id = "times_india",
            name = "Times of India",
            nameTelugu = "టైమ్స్ ఆఫ్ ఇండియా",
            category = "India",
            feedUrl = "https://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms"
        ),

        // Sports
        // TODO: verify feeds
        NewsFeed(
            id = "cricinfo_news",
            name = "ESPNCricinfo",
            nameTelugu = "ఇఎస్పీఎన్ క్రిక్‌ఇన్ఫో",
            category = "Sports",
            feedUrl = "https://www.espncricinfo.com/rss/content/story/feeds/0.xml"
        ),
        NewsFeed(
            id = "ndtv_cricket",
            name = "NDTV Cricket",
            nameTelugu = "ఎన్డీటీవీ క్రీడలు",
            category = "Sports",
            feedUrl = "https://feeds.feedburner.com/ndtvsports-cricket"
        ),
        NewsFeed(
            id = "hindu_sport",
            name = "The Hindu Sport",
            nameTelugu = "ది హిందూ స్పోర్ట్స్",
            category = "Sports",
            feedUrl = "https://www.thehindu.com/sport/feeder/default.rss"
        ),

        // Devotional
        // TODO: verify feeds
        NewsFeed(
            id = "speaking_tree",
            name = "Speaking Tree",
            nameTelugu = "స్పీకింగ్ ట్రీ (ఆధ్యాత్మికం)",
            category = "Devotional",
            feedUrl = "https://www.speakingtree.in/rss/allblogs.xml"
        ),
        NewsFeed(
            id = "hinduism_today",
            name = "Sanatana Dharma",
            nameTelugu = "సనాతన ధర్మం & భక్తి",
            category = "Devotional",
            feedUrl = "https://hinduismtoday.com/feed/"
        )
    )
}
