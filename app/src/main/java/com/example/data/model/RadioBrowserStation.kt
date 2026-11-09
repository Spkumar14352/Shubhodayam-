package com.example.data.model

data class RadioBrowserStation(
    val stationuuid: String,
    val name: String,
    val url: String,
    val urlResolved: String,
    val favicon: String = "",
    val tags: String = "",
    val language: String = "",
    val bitrate: Int = 128
) {
    fun getStreamUrl(): String {
        return if (urlResolved.isNotBlank()) urlResolved else url
    }

    fun getInitials(): String {
        val clean = name.trim().replace(Regex("[^A-Za-z0-9 ]"), "")
        val words = clean.split(" ").filter { it.isNotBlank() }
        return when {
            words.size >= 2 -> "${words[0].take(1)}${words[1].take(1)}".uppercase()
            clean.length >= 2 -> clean.take(2).uppercase()
            else -> "FM"
        }
    }
}

object FallbackRadioStations {
    val teluguStations = listOf(
        RadioBrowserStation(
            stationuuid = "telugu_1",
            name = "Telugu Devotional Chants (Tirumala)",
            url = "https://stream.zeno.fm/f9c8f0z9wqruv",
            urlResolved = "https://stream.zeno.fm/f9c8f0z9wqruv",
            favicon = "",
            tags = "devotional, chants, telugu",
            language = "telugu",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "telugu_2",
            name = "Telugu Golden Hits FM",
            url = "https://stream.zeno.fm/004hwd4b808uv",
            urlResolved = "https://stream.zeno.fm/004hwd4b808uv",
            favicon = "",
            tags = "telugu, hits, classics",
            language = "telugu",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "telugu_3",
            name = "Radio City Telugu",
            url = "https://stream.zeno.fm/w3k277u2w8quv",
            urlResolved = "https://stream.zeno.fm/w3k277u2w8quv",
            favicon = "",
            tags = "telugu, pop, news",
            language = "telugu",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "telugu_4",
            name = "Annamayya Sankeerthanas",
            url = "https://stream.zeno.fm/9k2c7h72x8quv",
            urlResolved = "https://stream.zeno.fm/9k2c7h72x8quv",
            favicon = "",
            tags = "devotional, annamayya, spiritual",
            language = "telugu",
            bitrate = 128
        )
    )

    val devotionalStations = listOf(
        RadioBrowserStation(
            stationuuid = "devo_1",
            name = "Sri Venkateswara Bhakthi Ganam",
            url = "https://stream.zeno.fm/f9c8f0z9wqruv",
            urlResolved = "https://stream.zeno.fm/f9c8f0z9wqruv",
            favicon = "",
            tags = "devotional, tirupati, vedic",
            language = "telugu",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "devo_2",
            name = "Shiva Chants & Maha Rudram",
            url = "https://stream.zeno.fm/s4k89qu9wqruv",
            urlResolved = "https://stream.zeno.fm/s4k89qu9wqruv",
            favicon = "",
            tags = "devotional, shiva, chants",
            language = "sanskrit",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "devo_3",
            name = "Divya Bhakthi Sagar",
            url = "https://stream.zeno.fm/d4k29qu9wqruv",
            urlResolved = "https://stream.zeno.fm/d4k29qu9wqruv",
            favicon = "",
            tags = "devotional, bhajans, kirtan",
            language = "hindi",
            bitrate = 128
        )
    )

    val nationalStations = listOf(
        RadioBrowserStation(
            stationuuid = "nat_1",
            name = "AIR Vividh Bharati National",
            url = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8",
            urlResolved = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8",
            favicon = "",
            tags = "national, all india radio, hindi",
            language = "hindi",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "nat_2",
            name = "Radio Mirchi India Hits",
            url = "https://stream.zeno.fm/9712z3u2w8quv",
            urlResolved = "https://stream.zeno.fm/9712z3u2w8quv",
            favicon = "",
            tags = "national, hits, bollywood",
            language = "hindi",
            bitrate = 128
        ),
        RadioBrowserStation(
            stationuuid = "nat_3",
            name = "AIR News 24x7 India",
            url = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio002/playlist.m3u8",
            urlResolved = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio002/playlist.m3u8",
            favicon = "",
            tags = "news, national, air",
            language = "english",
            bitrate = 128
        )
    )
}
