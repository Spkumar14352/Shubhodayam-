package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RssArticle(
    val id: String,
    val title: String,
    val link: String,
    val guid: String = "",
    val pubDate: String = "",
    val description: String = "",
    val content: String = "",
    val author: String = "",
    val thumbnail: String = "",
    val sourceName: String = "",
    val category: String = "Telugu",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getTimeAgo(isTelugu: Boolean): String {
        val diffMs = System.currentTimeMillis() - timestamp
        val diffMins = (diffMs / (1000 * 60)).coerceAtLeast(0)
        return when {
            diffMins < 1 -> if (isTelugu) "ఇప్పుడే" else "Just now"
            diffMins < 60 -> if (isTelugu) "$diffMins నిమిషాల క్రితం" else "$diffMins min ago"
            diffMins < 1440 -> {
                val hours = diffMins / 60
                if (isTelugu) "$hours గంటల క్రితం" else "$hours hrs ago"
            }
            else -> {
                val days = diffMins / 1440
                if (isTelugu) "$days రోజుల క్రితం" else "$days days ago"
            }
        }
    }

    fun cleanDescription(): String {
        // Strip HTML tags if any from description
        val stripped = description.replace(Regex("<[^>]*>"), "").trim()
        return if (stripped.length > 180) {
            stripped.take(180) + "..."
        } else {
            stripped
        }
    }
}
