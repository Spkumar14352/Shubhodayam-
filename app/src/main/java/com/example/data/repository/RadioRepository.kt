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
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // In-memory cache per tab
    private val cache = mutableMapOf<String, List<RadioBrowserStation>>()

    suspend fun getStations(categoryKey: String): List<RadioBrowserStation> = withContext(Dispatchers.IO) {
        val normalizedKey = categoryKey.uppercase()
        cache[normalizedKey]?.let { cached ->
            if (cached.isNotEmpty()) return@withContext cached
        }

        val baseCurated = when (normalizedKey) {
            "ALL" -> FallbackRadioStations.popularTeluguStations + FallbackRadioStations.nationalStations
            "DEVOTIONAL" -> FallbackRadioStations.popularTeluguStations.filter { it.category == "DEVOTIONAL" }
            "MELODIES" -> FallbackRadioStations.popularTeluguStations.filter { it.category == "MELODIES" }
            "NEWS" -> FallbackRadioStations.popularTeluguStations.filter { it.category == "NEWS" }
            "NATIONAL" -> FallbackRadioStations.nationalStations
            else -> FallbackRadioStations.popularTeluguStations
        }

        // Try augmenting from radio-browser.info for broader discovery, gracefully falling back
        val searchTag = when (normalizedKey) {
            "DEVOTIONAL" -> "countrycode=IN&tag=devotional"
            "NATIONAL" -> "countrycode=IN&language=hindi"
            "NEWS" -> "countrycode=IN&tag=news"
            else -> "countrycode=IN&language=telugu"
        }

        val url = "https://de1.api.radio-browser.info/json/stations/search?$searchTag&limit=30&hidebroken=true&order=clickcount&reverse=true"

        val combined = baseCurated.toMutableList()
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "ShubhodayaApp/1.0 (Android; Devotional & Radio)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    val existingUuids = combined.map { it.stationuuid }.toSet()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val name = obj.optString("name", "").trim()
                        val streamUrl = obj.optString("url_resolved", obj.optString("url", ""))
                        val uuid = obj.optString("stationuuid", "rb_$i")

                        if (name.isNotBlank() && streamUrl.isNotBlank() && !existingUuids.contains(uuid)) {
                            combined.add(
                                RadioBrowserStation(
                                    stationuuid = uuid,
                                    name = name,
                                    url = obj.optString("url", streamUrl),
                                    urlResolved = streamUrl,
                                    favicon = obj.optString("favicon", ""),
                                    tags = obj.optString("tags", ""),
                                    language = obj.optString("language", "telugu"),
                                    bitrate = obj.optInt("bitrate", 128),
                                    teluguName = "",
                                    category = normalizedKey,
                                    frequencyOrCity = obj.optString("state", "India")
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore network failures and use curated stations
        }

        cache[normalizedKey] = combined
        combined
    }
}
