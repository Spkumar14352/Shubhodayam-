package com.example.services

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.BabyGender
import com.example.data.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiBabyName(val name: String, val telugu: String, val meaning: String)

sealed interface BabyNamesAiResult {
    data class Success(val names: List<AiBabyName>) : BabyNamesAiResult
    data object NotConfigured : BabyNamesAiResult
    data object Failed : BabyNamesAiResult
}

class BabyNamesAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun suggestNames(
        syllable: String,
        gender: BabyGender?,
        language: Language
    ): BabyNamesAiResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == PLACEHOLDER_KEY) {
            return@withContext BabyNamesAiResult.NotConfigured
        }

        try {
            val text = callGemini(apiKey, buildPrompt(syllable, gender, language))
            val names = parseNames(text).filter { startsWithSyllableConsonant(it.name, syllable) }
            if (names.isEmpty()) BabyNamesAiResult.Failed else BabyNamesAiResult.Success(names)
        } catch (e: Exception) {
            Log.w(TAG, "Gemini baby name request failed", e)
            BabyNamesAiResult.Failed
        }
    }

    private fun buildPrompt(syllable: String, gender: BabyGender?, language: Language): String {
        val who = when (gender) {
            BabyGender.BOY -> "boys"
            BabyGender.GIRL -> "girls"
            BabyGender.UNISEX -> "either boys or girls (unisex names)"
            null -> "boys or girls"
        }
        val meaningLanguage = if (language == Language.TELUGU) "Telugu" else "English"
        return """
            Suggest 20 Telugu or Sanskrit baby names for $who whose first syllable sounds like "$syllable".
            Only use real names with a widely accepted meaning. Do not invent names or meanings.
            Return only a JSON array of 20 objects with these keys:
            "name" (in English letters), "telugu" (in Telugu script), "meaning" (a few words in $meaningLanguage).
        """.trimIndent()
    }

    private fun callGemini(apiKey: String, prompt: String): String {
        val body = JSONObject()
            .put(
                "contents",
                JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.6)
                    .put("responseMimeType", "application/json")
            )

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent")
            .header("x-goog-api-key", apiKey)
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw RuntimeException("Gemini HTTP ${response.code}")
            val json = JSONObject(response.body?.string().orEmpty())
            return json.getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts").getJSONObject(0)
                .getString("text")
        }
    }

    companion object {
        private const val TAG = "BabyNamesAiService"
        private const val MODEL = "gemini-2.5-flash"
        private const val PLACEHOLDER_KEY = "MY_GEMINI_API_KEY"

        internal fun parseNames(text: String): List<AiBabyName> {
            val array = JSONArray(text.substring(text.indexOf('['), text.lastIndexOf(']') + 1))
            return (0 until array.length())
                .mapNotNull(array::optJSONObject)
                .map {
                    AiBabyName(
                        name = it.optString("name").trim(),
                        telugu = it.optString("telugu").trim(),
                        meaning = it.optString("meaning").trim()
                    )
                }
                .filter { it.name.isNotEmpty() && it.telugu.isNotEmpty() && it.meaning.isNotEmpty() }
                .distinctBy { it.name.lowercase() }
        }

        /** Spellings of the vowel vary (Keerthi/Ki), so only the opening consonant is checked. */
        internal fun startsWithSyllableConsonant(name: String, syllable: String): Boolean =
            name.firstOrNull()?.equals(syllable.first(), ignoreCase = true) == true
    }
}
