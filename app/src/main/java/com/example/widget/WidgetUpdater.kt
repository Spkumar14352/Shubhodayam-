package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.data.AstronomyEngine
import com.example.data.model.City
import com.example.data.festivals.FestivalCalendar
import com.example.data.model.Language
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Saves the Panchang shown by the home-screen widget and asks the widget to redraw. */
object WidgetUpdater {
    const val PREFS_FILE = "CapacitorStorage"
    const val KEY_PANCHANG = "widget_panchang"

    // Referenced by name so the app still builds before the widget files are copied in.
    private const val PROVIDER_CLASS = "com.example.widget.PanchangWidgetProvider"

    private val hourMinute = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    fun saveAndRefresh(context: Context, language: Language, city: City) {
        val json = buildJson(context, LocalDate.now(), language, city).toString()
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PANCHANG, json)
            .apply()
        refresh(context)
    }

    /** Redraws every placed widget. Does nothing if the user hasn't added one. */
    fun refresh(context: Context) {
        val manager = AppWidgetManager.getInstance(context) ?: return
        val provider = ComponentName(context, PROVIDER_CLASS)
        val widgetIds = manager.getAppWidgetIds(provider)
        if (widgetIds.isEmpty()) return

        context.sendBroadcast(
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .setComponent(provider)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        )
    }

    internal fun buildJson(context: Context, today: LocalDate, language: Language, city: City): JSONObject =
        dayJson(context, today, language, city)
            .put("tomorrow", dayJson(context, today.plusDays(1), language, city))

    private fun dayJson(context: Context, date: LocalDate, language: Language, city: City): JSONObject {
        val isTelugu = language == Language.TELUGU
        val panchang = AstronomyEngine.calculatePanchang(date, city.latitude, city.longitude)
        val festival = FestivalCalendar.on(context, date, city.latitude, city.longitude).firstOrNull()
        val rahu = panchang.rahuKalam

        return JSONObject()
            .put("date", date.toString())
            .put("lang", language.code)
            .put("vaaram", if (isTelugu) panchang.vaaramTe else panchang.vaaramEn)
            .put("tithi", if (isTelugu) panchang.tithiTe else panchang.tithiEn)
            .put("tithiEnds", panchang.tithiEndDateTime.format(hourMinute))
            .put("tithiEndsNextDay", panchang.tithiEndDateTime.toLocalDate().isAfter(date))
            .put("nakshatra", if (isTelugu) panchang.nakshatraTe else panchang.nakshatraEn)
            .put("nakshatraEnds", panchang.nakshatraEndDateTime.format(hourMinute))
            .put("nakshatraEndsNextDay", panchang.nakshatraEndDateTime.toLocalDate().isAfter(date))
            .put("sunrise", panchang.sunMoonTimes.sunrise.format(hourMinute))
            .put("sunset", panchang.sunMoonTimes.sunset.format(hourMinute))
            .put("rahuKalam", "${rahu.startTime.format(hourMinute)} - ${rahu.endTime.format(hourMinute)}")
            .put("festival", festival?.let { if (isTelugu) it.nameTe else it.nameEn }.orEmpty())
    }
}
