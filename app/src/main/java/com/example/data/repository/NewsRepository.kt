package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.NewsFeed
import com.example.data.model.NewsFeedsData
import com.example.data.model.RssArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class NewsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shubhodaya_news_cache", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Get news for category: "Telugu", "India", "Sports", "Devotional"
     * Checks cached results first, fetches fresh via rss2json endpoint, and saves to cache.
     */
    suspend fun getNewsForCategory(category: String, forceRefresh: Boolean = false): List<RssArticle> = withContext(Dispatchers.IO) {
        val cacheKey = "cache_$category"
        val cachedJson = prefs.getString(cacheKey, null)
        val lastUpdated = prefs.getLong("time_$category", 0L)

        // If not forced and cached less than 15 minutes ago, return cached articles
        val fifteenMinutes = 15 * 60 * 1000L
        if (!forceRefresh && cachedJson != null && (System.currentTimeMillis() - lastUpdated) < fifteenMinutes) {
            val cachedArticles = parseArticlesJson(cachedJson)
            if (cachedArticles.isNotEmpty()) {
                return@withContext cachedArticles
            }
        }

        // Fetch from feeds matching the category
        val feeds = NewsFeedsData.feeds.filter { it.category.equals(category, ignoreCase = true) }
        val freshArticles = mutableListOf<RssArticle>()

        for (feed in feeds) {
            try {
                val articles = fetchRssFeed(feed)
                freshArticles.addAll(articles)
            } catch (e: Exception) {
                Log.w("NewsRepository", "Failed fetching feed ${feed.name}: ${e.message}")
            }
        }

        if (freshArticles.isNotEmpty()) {
            // Sort newest first
            freshArticles.sortByDescending { it.timestamp }
            // Save to cache
            saveArticlesToCache(category, freshArticles)
            return@withContext freshArticles
        }

        // If fetch failed or network offline, return cached if available
        if (cachedJson != null) {
            val cachedArticles = parseArticlesJson(cachedJson)
            if (cachedArticles.isNotEmpty()) {
                return@withContext cachedArticles
            }
        }

        // Fallback default sample articles if no network and no cache
        return@withContext getCategoryFallbacks(category)
    }

    suspend fun fetchRssFeed(feed: NewsFeed): List<RssArticle> = withContext(Dispatchers.IO) {
        val encodedUrl = URLEncoder.encode(feed.feedUrl, StandardCharsets.UTF_8.toString())
        val endpoint = "https://api.rss2json.com/v1/api.json?rss_url=$encodedUrl"

        val request = Request.Builder()
            .url(endpoint)
            .header("User-Agent", "ShubhodayaAndroid/1.0")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext emptyList()
            val bodyString = response.body?.string() ?: return@withContext emptyList()

            val json = JSONObject(bodyString)
            if (json.optString("status") != "ok") return@withContext emptyList()

            val itemsArray = json.optJSONArray("items") ?: return@withContext emptyList()
            val results = mutableListOf<RssArticle>()

            for (i in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(i)
                val title = item.optString("title").trim()
                if (title.isBlank()) continue

                val link = item.optString("link")
                val guid = item.optString("guid", link)
                val pubDate = item.optString("pubDate")
                val description = item.optString("description")
                val content = item.optString("content")
                val author = item.optString("author")
                val thumbnail = item.optString("thumbnail").ifBlank {
                    // check enclosure
                    item.optJSONObject("enclosure")?.optString("link") ?: ""
                }

                // parse pubDate to timestamp if possible
                val parsedTs = parseDateToTs(pubDate)

                results.add(
                    RssArticle(
                        id = "${feed.id}_$i",
                        title = title,
                        link = link,
                        guid = guid,
                        pubDate = pubDate,
                        description = description,
                        content = content,
                        author = author,
                        thumbnail = thumbnail,
                        sourceName = feed.nameTelugu.ifBlank { feed.name },
                        category = feed.category,
                        timestamp = parsedTs
                    )
                )
            }
            results
        }
    }

    fun getLastUpdatedTime(category: String): Long {
        return prefs.getLong("time_$category", 0L)
    }

    private fun saveArticlesToCache(category: String, articles: List<RssArticle>) {
        try {
            val jsonArray = JSONArray()
            articles.take(50).forEach { a ->
                val obj = JSONObject().apply {
                    put("id", a.id)
                    put("title", a.title)
                    put("link", a.link)
                    put("guid", a.guid)
                    put("pubDate", a.pubDate)
                    put("description", a.description)
                    put("content", a.content)
                    put("author", a.author)
                    put("thumbnail", a.thumbnail)
                    put("sourceName", a.sourceName)
                    put("category", a.category)
                    put("timestamp", a.timestamp)
                }
                jsonArray.put(obj)
            }
            prefs.edit()
                .putString("cache_$category", jsonArray.toString())
                .putLong("time_$category", System.currentTimeMillis())
                .apply()
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error saving news cache: ${e.message}")
        }
    }

    private fun parseArticlesJson(jsonString: String): List<RssArticle> {
        val list = mutableListOf<RssArticle>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RssArticle(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        link = obj.optString("link"),
                        guid = obj.optString("guid"),
                        pubDate = obj.optString("pubDate"),
                        description = obj.optString("description"),
                        content = obj.optString("content"),
                        author = obj.optString("author"),
                        thumbnail = obj.optString("thumbnail"),
                        sourceName = obj.optString("sourceName"),
                        category = obj.optString("category"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error parsing cached news: ${e.message}")
        }
        return list
    }

    private fun parseDateToTs(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        val formats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                val parsed = sdf.parse(dateStr)
                if (parsed != null) return parsed.time
            } catch (_: Exception) { }
        }
        return System.currentTimeMillis()
    }

    private fun getCategoryFallbacks(category: String): List<RssArticle> {
        val now = System.currentTimeMillis()
        return when (category) {
            "Telugu" -> listOf(
                RssArticle(
                    id = "te_1",
                    title = "ఆంధ్రప్రదేశ్, తెలంగాణలో సంక్షేమ పథకాలు వేగవంతం",
                    link = "https://www.eenadu.net",
                    description = "తెలుగు రాష్ట్రాల్లో అభివృద్ధి పనులకు శ్రీకారం. తాజా విధివిధానాలు ప్రకటన.",
                    sourceName = "ఈనాడు",
                    category = "Telugu",
                    timestamp = now - 15 * 60 * 1000L
                ),
                RssArticle(
                    id = "te_2",
                    title = "తిరుమల శ్రీవారి ఆలయంలో భక్తుల రద్దీ సాధారణం",
                    link = "https://www.sakshi.com",
                    description = "స్వామివారి దర్శనానికి 4 గంటల సమయం. క్యూలైన్లలో భక్తులకు నిరంతర అన్నప్రసాద వితరణ.",
                    sourceName = "సాక్షి",
                    category = "Telugu",
                    timestamp = now - 45 * 60 * 1000L
                )
            )
            "India" -> listOf(
                RssArticle(
                    id = "in_1",
                    title = "India steps up green energy initiatives nationwide",
                    link = "https://www.thehindu.com",
                    description = "Government announces expanded solar grid connectivity and electric transport subsidies.",
                    sourceName = "The Hindu",
                    category = "India",
                    timestamp = now - 20 * 60 * 1000L
                )
            )
            "Sports" -> listOf(
                RssArticle(
                    id = "sp_1",
                    title = "Team India commences preparation for upcoming international series",
                    link = "https://www.espncricinfo.com",
                    description = "Intensive net sessions underway with emphasis on pace bowling and spin variations.",
                    sourceName = "ESPNCricinfo",
                    category = "Sports",
                    timestamp = now - 30 * 60 * 1000L
                )
            )
            "Devotional" -> listOf(
                RssArticle(
                    id = "dev_1",
                    title = "కార్తీక మాస విశిష్టత & దీపారాధన ఫలం",
                    link = "https://www.speakingtree.in",
                    description = "శివ కేశవుల పూజలు, సోమవార ఉపవాస నియమాలు మరియు విశేష పుణ్యఫలాలు.",
                    sourceName = "ఆధ్యాత్మికం",
                    category = "Devotional",
                    timestamp = now - 60 * 60 * 1000L
                )
            )
            else -> emptyList()
        }
    }
}
