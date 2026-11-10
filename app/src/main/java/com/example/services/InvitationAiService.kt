package com.example.services

import android.util.Log
import com.example.BuildConfig
import com.example.data.invitation.Invitation
import com.example.data.invitation.Occasion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed interface InvitationAiResult {
    data class Success(val text: String) : InvitationAiResult
    data object NotConfigured : InvitationAiResult
    data object Failed : InvitationAiResult
}

/** Asks Gemini for a short, traditional invitation paragraph built only from the user's details. */
class InvitationAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun write(invitation: Invitation, telugu: Boolean): InvitationAiResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return@withContext InvitationAiResult.NotConfigured
        try {
            val text = limitWords(callGemini(apiKey, prompt(invitation, telugu)), MAX_WORDS)
            if (text.isBlank()) InvitationAiResult.Failed else InvitationAiResult.Success(text)
        } catch (e: Exception) {
            Log.w(TAG, "Gemini invitation request failed", e)
            InvitationAiResult.Failed
        }
    }

    private fun prompt(invitation: Invitation, telugu: Boolean): String {
        val details = listOfNotNull(
            "Occasion: ${invitation.occasion.nameEn}",
            invitation.primaryName.takeIf { it.isNotBlank() }?.let {
                (if (invitation.occasion == Occasion.WEDDING) "Bride: " else "Name: ") + it +
                    invitation.primaryParents.takeIf { p -> p.isNotBlank() }?.let { p -> " (parents: $p)" }.orEmpty()
            },
            invitation.secondaryName.takeIf { it.isNotBlank() && invitation.occasion == Occasion.WEDDING }?.let {
                "Groom: $it" + invitation.secondaryParents.takeIf { p -> p.isNotBlank() }?.let { p -> " (parents: $p)" }.orEmpty()
            },
            invitation.date?.let { "Date: $it" + (invitation.time?.let { t -> " at $t" } ?: "") },
            invitation.venueName.takeIf { it.isNotBlank() }?.let { "Venue: $it" }
        ).joinToString("\n")
        val language = if (telugu) "Telugu (in Telugu script)" else "English"
        return """
            Write a traditional, warm and respectful invitation paragraph in $language for this family function.
            Use at most $MAX_WORDS words. Use only the details below; do not invent names, dates or places.
            Output only the paragraph, with no title, quotes or markdown.

            $details
        """.trimIndent()
    }

    private fun callGemini(apiKey: String, prompt: String): String {
        val body = JSONObject()
            .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))
            .put("generationConfig", JSONObject().put("temperature", 0.7))
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent")
            .header("x-goog-api-key", apiKey)
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw RuntimeException("Gemini HTTP ${response.code}")
            return JSONObject(response.body?.string().orEmpty())
                .getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts").getJSONObject(0)
                .getString("text")
        }
    }

    companion object {
        private const val TAG = "InvitationAiService"
        private const val MODEL = "gemini-2.5-flash"
        const val MAX_WORDS = 60

        /** Trims quotes and markdown and cuts to [max] words. */
        internal fun limitWords(text: String, max: Int): String {
            val words = text.trim().trim('"', '“', '”', '*', '`').split(Regex("\\s+")).filter { it.isNotEmpty() }
            return words.take(max).joinToString(" ")
        }
    }
}
