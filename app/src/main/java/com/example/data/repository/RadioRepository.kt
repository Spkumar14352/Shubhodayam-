package com.example.data.repository

import com.example.data.model.FallbackRadioStations
import com.example.data.model.RadioBrowserStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class RadioRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // In-memory cache per tab
    private val cache = mutableMapOf<String, List<RadioBrowserStation>>()

    suspend fun getStations(categoryKey: String): List<RadioBrowserStation> = withContext(Dispatchers.IO) {
        cache[categoryKey]?.let { cached ->
            if (cached.isNotEmpty()) return@withContext cached
        }

        val url = when (categoryKey) {
            "DEVOTIONAL" -> "https://de1.api.radio-browser.info/json/stations/search?countrycode=IN&tag=devotional&limit=50&hidebroken=true&order=clickcount&reverse=true"
            "NATIONAL" -> "https://de1.api.radio-browser.info/json/stations/search?countrycode=IN&language=hindi&limit=50&hidebroken=true&order=clickcount&reverse=true"
            else -> "https://de1.api.radio-browser.info/json/stations/search?countrycode=IN&language=telugu&limit=50&hidebroken=true&order=clickcount&reverse=true"
        }

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "ShubhodayaApp/1.0 (Android; Devotional & News)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    val list = mutableListOf<RadioBrowserStation>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val name = obj.optString("name", "").trim()
                        val streamUrl = obj.optString("url_resolved", obj.optString("url", ""))

                        if (name.isNotBlank() && streamUrl.isNotBlank()) {
                            list.add(
                                RadioBrowserStation(
                                    stationuuid = obj.optString("stationuuid", "st_$i"),
                                    name = name,
                                    url = obj.optString("url", streamUrl),
                                    urlResolved = streamUrl,
                                    favicon = obj.optString("favicon", ""),
                                    tags = obj.optString("tags", ""),
                                    language = obj.optString("language", ""),
                                    bitrate = obj.optInt("bitrate", 128)
                                )
                            )
                        }
                    }

                    if (list.isNotEmpty()) {
                        cache[categoryKey] = list
                        return@withContext list
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore and use fallback
        }

        // Return curated fallbacks if network is offline or empty
        val fallback = when (categoryKey) {
            "DEVOTIONAL" -> FallbackRadioStations.devotionalStations
            "NATIONAL" -> FallbackRadioStations.nationalStations
            else -> FallbackRadioStations.teluguStations
        }
        cache[categoryKey] = fallback
        fallback
    }
}
