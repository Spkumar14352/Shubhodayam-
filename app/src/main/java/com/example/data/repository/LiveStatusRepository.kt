package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LiveStatus(
    val channelId: String,
    val isLive: Boolean,
    val videoId: String?,
    val title: String?,
    val thumbnail: String?
)

/** Where live status comes from. */
enum class LiveSource {
    /** Our Supabase "live-status" function, which keeps the YouTube key off the device. */
    SERVER,

    /** The YouTube Data API, called directly with YOUTUBE_API_KEY from local.properties. */
    YOUTUBE_API,

    /** Nothing configured: channels are listed with a "Watch on YouTube" button. */
    NONE
}

interface LiveStatusSource {
    val source: LiveSource

    /** Every channel at once (SERVER only). Null when the check failed. */
    suspend fun fetchAll(): Map<String, LiveStatus>?

    /** One channel (YOUTUBE_API only). Null when the check failed. */
    suspend fun fetchChannel(channelId: String): LiveStatus?
}

/** Network calls run on Dispatchers.IO and never throw: any failure is logged and returned as null. */
class LiveStatusRepository(
    serverUrl: String = BuildConfig.LIVE_STATUS_URL,
    apiKey: String = BuildConfig.YOUTUBE_API_KEY,
    private val client: OkHttpClient = defaultClient
) : LiveStatusSource {
    private val serverHttpUrl: HttpUrl? = serverUrl.trim()
        .takeIf { it.isNotEmpty() && it != PLACEHOLDER_URL }
        ?.let { url -> url.toHttpUrlOrNull().also { if (it == null) Log.w(TAG, "LIVE_STATUS_URL is not a valid http(s) URL; ignoring it") } }
    private val key: String = apiKey.trim().takeIf { it.isNotEmpty() && it != PLACEHOLDER_KEY }.orEmpty()

    override val source: LiveSource = when {
        serverHttpUrl != null -> LiveSource.SERVER
        key.isNotEmpty() -> LiveSource.YOUTUBE_API
        else -> LiveSource.NONE
    }

    override suspend fun fetchAll(): Map<String, LiveStatus>? {
        val url = serverHttpUrl ?: return null
        return withContext(Dispatchers.IO) { get(url, "live-status server")?.let { body -> parseServer(body) } }
    }

    override suspend fun fetchChannel(channelId: String): LiveStatus? {
        if (key.isEmpty() || !CHANNEL_ID.matches(channelId)) return null
        return withContext(Dispatchers.IO) { get(youTubeSearchUrl(channelId, key), "YouTube API")?.let { body -> parseYouTubeSearch(body, channelId) } }
    }

    /** Body of a successful response, or null (logged) for any network or HTTP error. Call on Dispatchers.IO. */
    private fun get(url: HttpUrl, what: String): String? {
        return try {
            client.newCall(Request.Builder().url(url).build()).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    body
                } else {
                    // The URL is never logged because the YouTube one contains the API key.
                    Log.w(TAG, "$what returned HTTP ${response.code}: ${body.take(300)}")
                    null
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "$what request failed: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "LiveStatusRepository"
        private const val PLACEHOLDER_URL = "MY_LIVE_STATUS_URL"
        private const val PLACEHOLDER_KEY = "MY_YOUTUBE_API_KEY"
        val VIDEO_ID = Regex("[A-Za-z0-9_-]{11}")
        val CHANNEL_ID = Regex("UC[A-Za-z0-9_-]{22}")

        private val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        }

        /** search.list costs 100 quota units, so the app only ever checks one channel at a time. */
        internal fun youTubeSearchUrl(channelId: String, key: String): HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("www.googleapis.com")
            .addPathSegments("youtube/v3/search")
            .addQueryParameter("part", "snippet")
            .addQueryParameter("channelId", channelId)
            .addQueryParameter("eventType", "live")
            .addQueryParameter("type", "video")
            .addQueryParameter("maxResults", "1")
            .addQueryParameter("key", key)
            .build()

        /**
         * Our server's JSON array. Returns null for anything that isn't the expected shape (an error object,
         * HTML, junk). Bad items are skipped; video IDs that aren't real IDs count as not live.
         */
        internal fun parseServer(body: String): Map<String, LiveStatus>? = try {
            val array = JSONArray(body)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val channelId = item.optString("channelId").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val videoId = item.stringOrNull("videoId")?.takeIf(VIDEO_ID::matches)
                channelId to LiveStatus(
                    channelId = channelId,
                    isLive = item.optBoolean("isLive") && videoId != null,
                    videoId = videoId,
                    title = item.stringOrNull("title"),
                    thumbnail = item.stringOrNull("thumbnail")
                )
            }.toMap()
        } catch (e: Exception) {
            Log.w(TAG, "Unexpected live-status response: ${body.take(300)}", e)
            null
        }

        /** A YouTube search.list response. Null for an API error or unexpected JSON; empty items means not live. */
        internal fun parseYouTubeSearch(body: String, channelId: String): LiveStatus? = try {
            val root = JSONObject(body)
            val error = root.optJSONObject("error")
            when {
                error != null -> {
                    Log.w(TAG, "YouTube API error ${error.optInt("code")}: ${error.optString("message")}")
                    null
                }
                !root.has("items") -> {
                    Log.w(TAG, "YouTube API response has no items: ${body.take(300)}")
                    null
                }
                else -> {
                    val item = root.optJSONArray("items")?.optJSONObject(0)
                    val videoId = item?.optJSONObject("id")?.stringOrNull("videoId")?.takeIf(VIDEO_ID::matches)
                    val snippet = item?.optJSONObject("snippet")
                    LiveStatus(
                        channelId = channelId,
                        isLive = videoId != null,
                        videoId = videoId,
                        title = snippet?.stringOrNull("title"),
                        thumbnail = snippet?.optJSONObject("thumbnails")?.optJSONObject("high")?.stringOrNull("url")
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unexpected YouTube API response: ${body.take(300)}", e)
            null
        }

        private fun JSONObject.stringOrNull(name: String): String? =
            if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }
    }
}
