package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CricketSampleData
import com.example.data.model.CricScore
import com.example.data.model.CricTeamInfo
import com.example.data.model.LiveCricketMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class CricketResult {
    data class Success(
        val matches: List<LiveCricketMatch>,
        val lastUpdatedMinutesAgo: Long,
        val isCached: Boolean
    ) : CricketResult()

    data class MissingApiKey(
        val message: String
    ) : CricketResult()

    data class Error(
        val message: String,
        val fallbackMatches: List<LiveCricketMatch>
    ) : CricketResult()
}

class CricketRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shubhodaya_cricket_cache", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    private val cacheDurationMs = 5 * 60 * 1000L // 5 minutes cache to stay within free daily quota

    suspend fun getCurrentMatches(forceRefresh: Boolean = false): CricketResult = withContext(Dispatchers.IO) {
        val rawKey = try {
            BuildConfig.CRICKET_API_KEY
        } catch (_: Exception) {
            ""
        }.trim()
        val apiKey = if (rawKey == "MY_CRICKET_API_KEY" || rawKey.isEmpty()) "" else rawKey

        val cachedJson = prefs.getString("matches_json", null)
        val lastFetchTime = prefs.getLong("last_fetch_time", 0L)
        val now = System.currentTimeMillis()
        val elapsed = now - lastFetchTime
        val elapsedMinutes = (elapsed / 60000).coerceAtLeast(0)

        // 1. If cached response is still fresh (< 5 mins) and not forceRefresh, return cache
        if (!forceRefresh && cachedJson != null && elapsed < cacheDurationMs) {
            val cachedList = parseMatchesJson(cachedJson)
            if (cachedList.isNotEmpty()) {
                val ordered = orderMatches(cachedList)
                return@withContext CricketResult.Success(
                    matches = ordered,
                    lastUpdatedMinutesAgo = elapsedMinutes,
                    isCached = true
                )
            }
        }

        // 2. If API Key is missing or empty
        if (apiKey.isBlank()) {
            // Return cached or sample matches
            if (cachedJson != null) {
                val cachedList = parseMatchesJson(cachedJson)
                if (cachedList.isNotEmpty()) {
                    return@withContext CricketResult.Success(
                        matches = orderMatches(cachedList),
                        lastUpdatedMinutesAgo = elapsedMinutes,
                        isCached = true
                    )
                }
            }
            return@withContext CricketResult.MissingApiKey(
                message = "CRICKET_API_KEY is not configured in .env or Secrets. Showing demo match scores."
            )
        }

        // 3. Call CricketData.org endpoint
        val url = "https://api.cricapi.com/v1/currentMatches?apikey=$apiKey&offset=0"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "ShubhodayaAndroid/1.0")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext handleApiError("API HTTP error: ${response.code}", cachedJson, elapsedMinutes)
                }
                val bodyString = response.body?.string() ?: return@withContext handleApiError("Empty response", cachedJson, elapsedMinutes)
                val json = JSONObject(bodyString)

                if (json.optString("status") != "success") {
                    val errMsg = json.optString("reason", json.optString("message", "API status failed"))
                    return@withContext handleApiError(errMsg, cachedJson, elapsedMinutes)
                }

                val dataArray = json.optJSONArray("data")
                if (dataArray == null || dataArray.length() == 0) {
                    return@withContext handleApiError("No matches currently available", cachedJson, elapsedMinutes)
                }

                val matches = mutableListOf<LiveCricketMatch>()
                for (i in 0 until dataArray.length()) {
                    val mObj = dataArray.getJSONObject(i)
                    matches.add(parseSingleMatch(mObj))
                }

                // Cache the fresh response
                prefs.edit()
                    .putString("matches_json", dataArray.toString())
                    .putLong("last_fetch_time", now)
                    .apply()

                val ordered = orderMatches(matches)
                return@withContext CricketResult.Success(
                    matches = ordered,
                    lastUpdatedMinutesAgo = 0,
                    isCached = false
                )
            }
        } catch (e: Exception) {
            Log.e("CricketRepository", "Exception fetching cricket scores: ${e.message}")
            return@withContext handleApiError(e.message ?: "Network error", cachedJson, elapsedMinutes)
        }
    }

    private fun handleApiError(errorMsg: String, cachedJson: String?, elapsedMinutes: Long): CricketResult {
        if (cachedJson != null) {
            val cachedList = parseMatchesJson(cachedJson)
            if (cachedList.isNotEmpty()) {
                return CricketResult.Success(
                    matches = orderMatches(cachedList),
                    lastUpdatedMinutesAgo = elapsedMinutes,
                    isCached = true
                )
            }
        }
        return CricketResult.Error(
            message = errorMsg,
            fallbackMatches = CricketSampleData.sampleMatches
        )
    }

    /**
     * Requirement: Show live matches first (team names, score, status), then upcoming and recent.
     */
    fun orderMatches(matches: List<LiveCricketMatch>): List<LiveCricketMatch> {
        val live = matches.filter { it.isLive }
        val upcoming = matches.filter { it.isUpcoming }
        val recent = matches.filter { it.isRecent }
        val others = matches.filter { !it.isLive && !it.isUpcoming && !it.isRecent }
        return live + upcoming + recent + others
    }

    private fun parseMatchesJson(jsonString: String): List<LiveCricketMatch> {
        val list = mutableListOf<LiveCricketMatch>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(parseSingleMatch(obj))
            }
        } catch (e: Exception) {
            Log.e("CricketRepository", "Error parsing cached cricket matches: ${e.message}")
        }
        return list
    }

    private fun parseSingleMatch(obj: JSONObject): LiveCricketMatch {
        val id = obj.optString("id")
        val name = obj.optString("name")
        val matchType = obj.optString("matchType")
        val status = obj.optString("status")
        val venue = obj.optString("venue")
        val date = obj.optString("date")
        val dateTimeGMT = obj.optString("dateTimeGMT")
        val matchStarted = obj.optBoolean("matchStarted", false)
        val matchEnded = obj.optBoolean("matchEnded", false)

        val teamsList = mutableListOf<String>()
        val teamsArr = obj.optJSONArray("teams")
        if (teamsArr != null) {
            for (t in 0 until teamsArr.length()) {
                teamsList.add(teamsArr.getString(t))
            }
        }

        val teamInfoList = mutableListOf<CricTeamInfo>()
        val teamInfoArr = obj.optJSONArray("teamInfo")
        if (teamInfoArr != null) {
            for (ti in 0 until teamInfoArr.length()) {
                val tObj = teamInfoArr.getJSONObject(ti)
                teamInfoList.add(
                    CricTeamInfo(
                        name = tObj.optString("name"),
                        shortname = tObj.optString("shortname"),
                        img = tObj.optString("img")
                    )
                )
            }
        }

        val scoresList = mutableListOf<CricScore>()
        val scoreArr = obj.optJSONArray("score")
        if (scoreArr != null) {
            for (s in 0 until scoreArr.length()) {
                val sObj = scoreArr.getJSONObject(s)
                scoresList.add(
                    CricScore(
                        r = sObj.optInt("r", 0),
                        w = sObj.optInt("w", 0),
                        o = sObj.optDouble("o", 0.0),
                        inning = sObj.optString("inning")
                    )
                )
            }
        }

        return LiveCricketMatch(
            id = id,
            name = name,
            matchType = matchType,
            status = status,
            venue = venue,
            date = date,
            dateTimeGMT = dateTimeGMT,
            teams = teamsList,
            teamInfo = teamInfoList,
            score = scoresList,
            matchStarted = matchStarted,
            matchEnded = matchEnded,
            tournament = matchType.uppercase()
        )
    }
}
