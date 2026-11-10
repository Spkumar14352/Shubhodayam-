package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.concurrent.TimeUnit

data class LiveStatus(
    val channelId: String,
    val isLive: Boolean,
    val videoId: String?,
    val title: String?,
    val thumbnail: String?
)

sealed interface LiveStatusState {
    data object Loading : LiveStatusState
    data class Ready(val channels: Map<String, LiveStatus>) : LiveStatusState

    /** The check failed, which says nothing about whether a channel is live. */
    data object Unavailable : LiveStatusState
}

class LiveStatusRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetch(): LiveStatusState = withContext(Dispatchers.IO) {
        val url = BuildConfig.LIVE_STATUS_URL
        if (url.isBlank() || url == PLACEHOLDER_URL) return@withContext LiveStatusState.Unavailable

        try {
            client.newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
                LiveStatusState.Ready(parse(response.body?.string().orEmpty()))
            }
        } catch (e: IOException) {
            Log.w(TAG, "Live status request failed", e)
            LiveStatusState.Unavailable
        } catch (e: JSONException) {
            Log.w(TAG, "Live status response was invalid", e)
            LiveStatusState.Unavailable
        }
    }

    companion object {
        private const val TAG = "LiveStatusRepository"
        private const val PLACEHOLDER_URL = "MY_LIVE_STATUS_URL"
        private val VIDEO_ID = Regex("[A-Za-z0-9_-]{11}")

        /** Video IDs go into player HTML, so anything that isn't a real ID counts as not live. */
        internal fun parse(body: String): Map<String, LiveStatus> {
            val array = JSONArray(body)
            return (0 until array.length()).associate { index ->
                val item = array.getJSONObject(index)
                val channelId = item.getString("channelId")
                val videoId = item.optString("videoId").takeIf(VIDEO_ID::matches)
                channelId to LiveStatus(
                    channelId = channelId,
                    isLive = item.optBoolean("isLive") && videoId != null,
                    videoId = videoId,
                    title = item.optString("title").takeIf { !item.isNull("title") && it.isNotBlank() },
                    thumbnail = item.optString("thumbnail").takeIf { !item.isNull("thumbnail") && it.isNotBlank() }
                )
            }
        }
    }
}
