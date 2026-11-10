package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PanchangWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val views = buildViews(context)
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
    }

    private fun buildViews(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_panchang)
        views.setOnClickPendingIntent(R.id.widget_root, openAppIntent(context))

        val saved = readSaved(context)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val day = saved?.let { pickDay(it, today) }
        val isTelugu = saved?.optString("lang") != "en"

        views.setTextViewText(R.id.widget_title, if (isTelugu) "నేటి పంచాంగం" else "Today's Panchang")

        if (day == null) showOpenAppMessage(views, isTelugu) else showDay(views, day, isTelugu)
        return views
    }

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun readSaved(context: Context): JSONObject? {
        val raw = context.getSharedPreferences(WidgetUpdater.PREFS_FILE, Context.MODE_PRIVATE)
            .getString(WidgetUpdater.KEY_PANCHANG, null) ?: return null
        return try {
            JSONObject(raw)
        } catch (_: JSONException) {
            null
        }
    }

    /** Returns the block saved for [today], or null when the saved data is out of date. */
    private fun pickDay(saved: JSONObject, today: String): JSONObject? {
        if (saved.optString("date") == today) return saved
        return saved.optJSONObject("tomorrow")?.takeIf { it.optString("date") == today }
    }

    private fun showOpenAppMessage(views: RemoteViews, isTelugu: Boolean) {
        views.setViewVisibility(R.id.widget_rows, View.GONE)
        views.setViewVisibility(R.id.widget_message, View.VISIBLE)
        views.setTextViewText(R.id.widget_vaaram, "")
        views.setTextViewText(
            R.id.widget_message,
            if (isTelugu) "నేటి పంచాంగం కోసం యాప్ తెరవండి" else "Open the app to load today's Panchang"
        )
    }

    private fun showDay(views: RemoteViews, day: JSONObject, isTelugu: Boolean) {
        views.setViewVisibility(R.id.widget_rows, View.VISIBLE)
        views.setViewVisibility(R.id.widget_message, View.GONE)
        views.setTextViewText(R.id.widget_vaaram, day.optString("vaaram"))

        views.setTextViewText(R.id.widget_label_tithi, if (isTelugu) "తిథి" else "Tithi")
        views.setTextViewText(
            R.id.widget_value_tithi,
            nameWithEnd(day, "tithi", "tithiEnds", "tithiEndsNextDay", isTelugu)
        )

        views.setTextViewText(R.id.widget_label_nakshatra, if (isTelugu) "నక్షత్రం" else "Nakshatra")
        views.setTextViewText(
            R.id.widget_value_nakshatra,
            nameWithEnd(day, "nakshatra", "nakshatraEnds", "nakshatraEndsNextDay", isTelugu)
        )

        views.setTextViewText(R.id.widget_label_rahu, if (isTelugu) "రాహుకాలం" else "Rahu Kalam")
        views.setTextViewText(R.id.widget_value_rahu, day.optString("rahuKalam"))

        val sunrise = day.optString("sunrise")
        val sunset = day.optString("sunset")
        views.setTextViewText(R.id.widget_label_sun, if (isTelugu) "సూర్యోదయం" else "Sunrise")
        views.setTextViewText(
            R.id.widget_value_sun,
            if (isTelugu) "$sunrise • అస్తమయం $sunset" else "$sunrise • Sunset $sunset"
        )

        val festival = day.optString("festival")
        views.setTextViewText(R.id.widget_festival, festival)
        views.setViewVisibility(
            R.id.widget_festival,
            if (festival.isEmpty()) View.GONE else View.VISIBLE
        )
    }

    private fun nameWithEnd(
        day: JSONObject,
        nameKey: String,
        endKey: String,
        nextDayKey: String,
        isTelugu: Boolean
    ): String {
        val end = day.optString(endKey)
        val endsNextDay = day.optBoolean(nextDayKey)
        val until = when {
            isTelugu && endsNextDay -> "రేపు $end వరకు"
            isTelugu -> "$end వరకు"
            endsNextDay -> "till $end tomorrow"
            else -> "till $end"
        }
        return "${day.optString(nameKey)} • $until"
    }
}
