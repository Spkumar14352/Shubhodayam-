package com.example.services

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GreetingCategory
import com.example.data.model.GreetingTemplate
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

class GreetingAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateGreetingMessage(
        template: GreetingTemplate,
        language: Language,
        userSenderName: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val generated = callGeminiApi(apiKey, template, language)
                if (generated.isNotBlank()) {
                    return@withContext cleanMessage(generated)
                }
            } catch (e: Exception) {
                Log.w("GreetingAiService", "Gemini API call failed, falling back to curated AI messages", e)
            }
        }

        // Curated fallback message suitable for the category & template (strictly under 25 words, warm & respectful)
        return@withContext getCuratedFallback(template, language)
    }

    private fun callGeminiApi(
        apiKey: String,
        template: GreetingTemplate,
        language: Language
    ): String {
        val targetLangName = if (language == Language.TELUGU) "Telugu" else "English"
        val prompt = """
            Write a short, warm, and polite greeting message in $targetLangName for a greeting card.
            Category: ${template.category.titleEn}
            Topic/Occasion: ${template.titleEn} (${template.titleTe})
            Rules:
            1. Under 25 words maximum.
            2. Warm, auspicious, and respectful.
            3. No religious claims, no supernatural promises, no superstitious fortune-telling.
            4. Output ONLY the greeting text, with no quotes, no markdown, and no preamble.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 80)
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Gemini HTTP error ${response.code}")
        }

        val respBody = response.body?.string() ?: throw RuntimeException("Empty response")
        val jsonResp = JSONObject(respBody)
        val candidates = jsonResp.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return parts.getJSONObject(0).optString("text", "")
            }
        }
        return ""
    }

    private fun cleanMessage(raw: String): String {
        var text = raw.trim().trim('"', '\'', '“', '”', '`', '*')
        // Ensure max 25 words
        val words = text.split("\\s+".toRegex())
        if (words.size > 25) {
            text = words.take(25).joinToString(" ") + "..."
        }
        return text
    }

    private fun getCuratedFallback(template: GreetingTemplate, language: Language): String {
        val tePool = when (template.category) {
            GreetingCategory.GOOD_MORNING -> listOf(
                "ఈ ఉదయం మీ జీవితంలో సరికొత్త ఉత్సాహాన్ని, ప్రశాంతతను నింపాలని మనసారా కోరుకుంటున్నాను. శుభోదయం!",
                "ప్రతి నూతన ఉషస్సు విజయానికి నాంది కావాలి. మీ రోజంతా ఆనందమయంగా సాగిపోవుగాక. శుభదినం!",
                "సూర్యకాంతిలా మీ మనస్సు తేజోవంతంగా ప్రకాశించాలని ఆశిస్తూ హృదయపూర్వక శుభోదయం.",
                "మంచి ఆలోచనలు, చిరునవ్వుతో ఈ రోజును ప్రారంభించండి. మీకు సకల శుభాలు కలుగుగాక."
            )
            GreetingCategory.GOOD_NIGHT -> listOf(
                "రోజంతా చేసిన శ్రమను మరచి, హాయిగా విశ్రాంతి తీసుకోండి. రేపటి ఉదయం సరికొత్త కాంతులు తేవాలి. శుభరాత్రి!",
                "చల్లని వెన్నెల మీ మనస్సుకు ప్రశాంతతను ఇవ్వాలని కోరుకుంటూ శుభరాత్రి. మధుర స్వప్నాలు మీ సొంతం కావాలి.",
                "ఈ రాత్రి మీకు అమృతతుల్యమైన నిద్రను, నూతన శక్తిని ప్రసాదించాలని ఆశిస్తూ శుభరాత్రి.",
                "అన్ని చింతలను వదిలేసి ప్రశాంతంగా నిద్రించండి. రేపటి దినం మీ కోసం వేచి ఉంది."
            )
            GreetingCategory.FESTIVALS -> listOf(
                "${template.titleTe} పర్వదినం మీ ఇంట ఆనందాల వెల్లువను, సుఖసంతోషాలను నింపాలని కోరుకుంటూ హృదయపూర్వక శుభాకాంక్షలు!",
                "పండుగ శోభ మీ కుటుంబంలో నిరంతర ఆనందాన్ని, సౌభాగ్యాన్ని తీసుకురావాలని ఆకాంక్షిస్తూ శుభాకాంక్షలు.",
                "ఈ పవిత్ర పండుగ మీకు, మీ కుటుంబ సభ్యులకు ఆయురారోగ్యాలు, ఐశ్వర్యాన్ని ప్రసాదించాలని ప్రార్థిస్తున్నాను.",
                "బంధుమిత్రుల ప్రేమానురాగాలతో మీ ఇల్లు కళకళలాడాలని కోరుకుంటూ పండుగ శుభాకాంక్షలు!"
            )
            GreetingCategory.BIRTHDAY -> listOf(
                "మీ జన్మదినం సందర్భంగా హృదయపూర్వక శుభాకాంక్షలు! రాబోయే కాలమంతా మీకు ఆయురారోగ్యాలు, విజయాలు కలగాలి.",
                "పుట్టినరోజు శుభాకాంక్షలు! మీ ప్రతి కల సాకారమై, జీవితం సుఖసంతోషాలతో నిండాలని ఆశిస్తున్నాను.",
                "ఈ ప్రత్యేకమైన రోజు మీ ముఖంలో చిరునవ్వులు, మనస్సులో సంతృప్తిని నింపాలని కోరుకుంటూ జన్మదిన శుభాకాంక్షలు!"
            )
            GreetingCategory.ANNIVERSARY -> listOf(
                "మీ దాంపత్య జీవితం నిరంతరం ప్రేమానురాగాలు, అన్యోన్యతతో వర్ధిల్లాలని కోరుకుంటూ వివాహ వార్షికోత్సవ శుభాకాంక్షలు!",
                "నూరేళ్ల పంటగా మీ బంధం పదిలంగా ఉండాలని, మీ ఇంట సుఖశాంతులు వెల్లివిరియాలని ఆశిస్తూ పెళ్లిరోజు శుభాకాంక్షలు!",
                "ఒకరికొకరు తోడునీడగా నిలిచే మీ ఆదర్శ జంటకు హృదయపూర్వక వివాహ వార్షికోత్సవ శుభాకాంక్షలు!"
            )
            GreetingCategory.DEVOTIONAL -> listOf(
                "దైవానుగ్రహంతో మీ ఇంట నిత్యం శాంతి, సమాధానం, ఆనందం వెల్లివిరియాలని మనస్ఫూర్తిగా ప్రార్థిస్తున్నాను.",
                "సర్వమంగళ ప్రదాత అయిన భగవంతుని కృపాకటాక్షాలు మీ కుటుంబంపై సదా ఉండాలని కోరుకుంటూ...",
                "పవిత్రమైన భక్తిభావన మీ మనస్సును నిర్మలంగా ఉంచి, సన్మార్గంలో నడిపించుగాక. ఓం శాంతిః!"
            )
        }

        val enPool = when (template.category) {
            GreetingCategory.GOOD_MORNING -> listOf(
                "May this radiant dawn bring boundless optimism, vibrant health, and peaceful success to your day. Good morning!",
                "Start this beautiful morning with a joyful heart and steady determination. Wishing you a truly blessed day!",
                "May the morning sunshine illuminate your home with warmth and prosperous energy. Have a wonderful day!",
                "Wishing you calm clarity and cheerful moments from sunrise to sunset. Good morning!"
            )
            GreetingCategory.GOOD_NIGHT -> listOf(
                "Unwind, release all worries, and drift into restorative slumber. Tomorrow brings fresh promise. Good night!",
                "May gentle moonlight cradle your home in tranquil serenity and sweet dreams. Sleep peacefully!",
                "End today with heartfelt gratitude and awaken tomorrow with vibrant energy. Good night!",
                "Wishing you silent, restful hours and an inspired awakening tomorrow morning."
            )
            GreetingCategory.FESTIVALS -> listOf(
                "Wishing you and your family abundant joy, sweet celebrations, and prosperity on ${template.titleEn}!",
                "May the vibrant spirit of this festive season bring harmony, laughter, and blessings to your home.",
                "Warmest festive greetings! May good fortune, peace, and togetherness illuminate your days.",
                "Celebrating traditions of unity and joy. Have a joyous and memorable celebration!"
            )
            GreetingCategory.BIRTHDAY -> listOf(
                "Wishing you a very Happy Birthday! May the upcoming year bring robust health, great triumphs, and endless joy.",
                "Happy Birthday! May your path be blessed with laughter, warm friendships, and delightful milestones.",
                "Celebrate this special milestone with gratitude and high spirits. Have a fantastic birthday!"
            )
            GreetingCategory.ANNIVERSARY -> listOf(
                "Happy Wedding Anniversary! May your sacred companionship grow stronger, sweeter, and more joyous with each passing year.",
                "Wishing a wonderful couple endless understanding, unconditional affection, and lasting bliss. Happy Anniversary!",
                "May your journey together continue to inspire everyone around you with love and harmony. Happy Anniversary!"
            )
            GreetingCategory.DEVOTIONAL -> listOf(
                "May divine grace and calm serenity surround your household with enduring peace and protection.",
                "Praying that sacred light dissolves all obstacles and fills your heart with boundless contentment.",
                "May truth, noble thoughts, and righteous deeds guide your steps today and always. Om Shanti!"
            )
        }

        val list = if (language == Language.TELUGU) tePool else enPool
        return list.random()
    }
}
