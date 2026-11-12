package com.example.data.model

data class RadioBrowserStation(
    val stationuuid: String,
    val name: String,
    val url: String,
    val urlResolved: String,
    val favicon: String = "",
    val tags: String = "",
    val language: String = "",
    val bitrate: Int = 128,
    val teluguName: String = "",
    val category: String = "POPULAR", // POPULAR, DEVOTIONAL, MELODIES, NEWS, NATIONAL
    val frequencyOrCity: String = ""
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
    val popularTeluguStations = listOf(
        RadioBrowserStation(
            stationuuid = "telugu_akashvani_hyd",
            name = "Akashvani Hyderabad (AIR Telugu)",
            teluguName = "ఆకాశవాణి హైదరాబాద్ తెలుగు",
            url = "https://radio.wavespb.com/live/1c701f7b70df2568/1c701f7b70df2568.m3u8",
            urlResolved = "https://radio.wavespb.com/live/1c701f7b70df2568/1c701f7b70df2568.m3u8",
            favicon = "",
            tags = "air, akashvani, telugu, hyderabad, news",
            language = "telugu",
            bitrate = 128,
            category = "NEWS",
            frequencyOrCity = "Hyderabad AIR"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_vb_hyd",
            name = "Vividh Bharati Telugu Hyderabad",
            teluguName = "వివిధ భారతి తెలుగు హైదరాబాద్",
            url = "https://radio.wavespb.com/live/89969ad91f106dde/89969ad91f106dde.m3u8",
            urlResolved = "https://radio.wavespb.com/live/89969ad91f106dde/89969ad91f106dde.m3u8",
            favicon = "",
            tags = "vividh bharati, film hits, retro, entertainment",
            language = "telugu",
            bitrate = 128,
            category = "POPULAR",
            frequencyOrCity = "102.8 FM Hyderabad"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_fm_rainbow_hyd",
            name = "FM Rainbow Hyderabad",
            teluguName = "ఎఫ్.ఎం రెయిన్‌బో హైదరాబాద్",
            url = "https://radio.wavespb.com/live/3a02c5c035815bb9/3a02c5c035815bb9.m3u8",
            urlResolved = "https://radio.wavespb.com/live/3a02c5c035815bb9/3a02c5c035815bb9.m3u8",
            favicon = "",
            tags = "rainbow, hits, chartbusters, pop, youth",
            language = "telugu",
            bitrate = 128,
            category = "POPULAR",
            frequencyOrCity = "101.9 FM Hyderabad"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_svbc_ttd",
            name = "SVBC TTD Radio (Tirumala Devotional)",
            teluguName = "తిరుమల శ్రీ వేంకటేశ్వర భక్తి ప్రసారాలు",
            url = "https://radio.mslivecdn.com:6278/stream",
            urlResolved = "https://radio.mslivecdn.com:6278/stream",
            favicon = "",
            tags = "devotional, suprabhatam, tirupati, ttd, sankeerthana",
            language = "telugu",
            bitrate = 128,
            category = "DEVOTIONAL",
            frequencyOrCity = "Tirumala Tirupati"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_shaiva_lahari",
            name = "Shaiva Lahari Bhakthi Ganam",
            teluguName = "శైవ లహరి భక్తి గానం & స్తోత్రాలు",
            url = "https://radio.shaivam.org/listen/shaiva-lahari/radio.mp3",
            urlResolved = "https://radio.shaivam.org/listen/shaiva-lahari/radio.mp3",
            favicon = "",
            tags = "devotional, shiva, chants, stotras, vedic",
            language = "telugu",
            bitrate = 128,
            category = "DEVOTIONAL",
            frequencyOrCity = "Devotional Chants"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_vb_vijayawada",
            name = "Vividh Bharati Telugu Vijayawada",
            teluguName = "వివిధ భారతి విజయవాడ తెలుగు",
            url = "https://radio.wavespb.com/live/0beac8e921bbfdbb/0beac8e921bbfdbb.m3u8",
            urlResolved = "https://radio.wavespb.com/live/0beac8e921bbfdbb/0beac8e921bbfdbb.m3u8",
            favicon = "",
            tags = "vividh bharati, vijayawada, melodies, film songs",
            language = "telugu",
            bitrate = 128,
            category = "MELODIES",
            frequencyOrCity = "103.5 FM Vijayawada"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_fm_rainbow_vja",
            name = "FM Rainbow Vijayawada",
            teluguName = "ఎఫ్.ఎం రెయిన్‌బో విజయవాడ",
            url = "https://radio.wavespb.com/live/bad174fd7eac38d0/bad174fd7eac38d0.m3u8",
            urlResolved = "https://radio.wavespb.com/live/bad174fd7eac38d0/bad174fd7eac38d0.m3u8",
            favicon = "",
            tags = "rainbow, vijayawada, hits, songs",
            language = "telugu",
            bitrate = 128,
            category = "POPULAR",
            frequencyOrCity = "102.2 FM Vijayawada"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_akashvani_tirupati",
            name = "Akashvani Tirupati",
            teluguName = "ఆకాశవాణి తిరుపతి",
            url = "https://radio.wavespb.com/live/1119aa26b4e9f653/1119aa26b4e9f653.m3u8",
            urlResolved = "https://radio.wavespb.com/live/1119aa26b4e9f653/1119aa26b4e9f653.m3u8",
            favicon = "",
            tags = "air, tirupati, devotional, cultural",
            language = "telugu",
            bitrate = 128,
            category = "DEVOTIONAL",
            frequencyOrCity = "Tirupati AIR"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_akashvani_vizag",
            name = "Akashvani Visakhapatnam",
            teluguName = "ఆకాశవాణి విశాఖపట్నం",
            url = "https://radio.wavespb.com/live/8362d532bd498fd0/8362d532bd498fd0.m3u8",
            urlResolved = "https://radio.wavespb.com/live/8362d532bd498fd0/8362d532bd498fd0.m3u8",
            favicon = "",
            tags = "air, vizag, andhra coast, news, culture",
            language = "telugu",
            bitrate = 128,
            category = "NEWS",
            frequencyOrCity = "Visakhapatnam AIR"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_oye_radio",
            name = "Oye Radio Telugu Hits",
            teluguName = "ఓయే రేడియో తెలుగు సూపర్ హిట్స్",
            url = "https://stream.oye.radio/listen/oye_radio/radio.mp3",
            urlResolved = "https://stream.oye.radio/listen/oye_radio/radio.mp3",
            favicon = "",
            tags = "oye, hits, modern, pop, cinema",
            language = "telugu",
            bitrate = 128,
            category = "POPULAR",
            frequencyOrCity = "Online Telugu Hits"
        ),
        RadioBrowserStation(
            stationuuid = "telugu_air_national_032",
            name = "AIR Telugu National Network",
            teluguName = "ఆకాశవాణి తెలుగు జాతీయ ప్రసారాలు",
            url = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio032/playlist.m3u8",
            urlResolved = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio032/playlist.m3u8",
            favicon = "",
            tags = "air, national, telugu news, varthalu",
            language = "telugu",
            bitrate = 128,
            category = "NEWS",
            frequencyOrCity = "All India Radio"
        )
    )

    // Backwards compatibility mappings for older callers
    val teluguStations: List<RadioBrowserStation> get() = popularTeluguStations
    val devotionalStations: List<RadioBrowserStation> get() = popularTeluguStations.filter { it.category == "DEVOTIONAL" }
    val nationalStations: List<RadioBrowserStation> get() = listOf(
        RadioBrowserStation(
            stationuuid = "nat_vividh_bharati",
            name = "AIR Vividh Bharati National",
            teluguName = "వివిధ భారతి నేషనల్",
            url = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8",
            urlResolved = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8",
            favicon = "",
            tags = "national, air, hindi, classic",
            language = "hindi",
            bitrate = 128,
            category = "NATIONAL",
            frequencyOrCity = "All India Radio"
        )
    )
}
