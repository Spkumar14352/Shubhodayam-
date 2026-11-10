package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.festivals.FestivalCalendar
import com.example.data.model.City
import com.example.data.model.Language
import com.example.data.model.PanchangData
import com.example.data.model.PanchangProvider
import com.example.data.repository.UserPreferences
import java.time.LocalDate

class LocalNotificationScheduler(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_PANCHANG_ID = "shubhodaya_panchang_channel"
        const val CHANNEL_RAHU_ID = "shubhodaya_rahu_channel"
        const val CHANNEL_FESTIVALS_ID = "shubhodaya_festivals_channel"

        const val NOTIFICATION_MORNING_ID = 1001
        const val NOTIFICATION_RAHU_ID = 1002
        const val NOTIFICATION_FESTIVAL_ID = 1003
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val panchangChannel = NotificationChannel(
                CHANNEL_PANCHANG_ID,
                "Daily Panchang (ఉదయకాల పంచాంగం)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily morning updates on Tithi, Nakshatra, and Rahu Kalam"
            }

            val rahuChannel = NotificationChannel(
                CHANNEL_RAHU_ID,
                "Rahu Kalam Reminders (రాహుకాల హెచ్చరికలు)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely notification 15 minutes before Rahu Kalam starts"
            }

            val festivalChannel = NotificationChannel(
                CHANNEL_FESTIVALS_ID,
                "Festival Reminders (పండుగల సమాచారం)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Evening reminders before major Telugu and Hindu festivals"
            }

            notificationManager.createNotificationChannel(panchangChannel)
            notificationManager.createNotificationChannel(rahuChannel)
            notificationManager.createNotificationChannel(festivalChannel)
        }
    }

    fun showMorningPanchangNotification(panchang: PanchangData, language: Language) {
        val isTelugu = language == Language.TELUGU
        val title = if (isTelugu) "శుభోదయం! నేటి పంచాంగం" else "Shubhodaya! Today's Panchang"
        val text = if (isTelugu) {
            "తిథి: ${panchang.tithiTe} | నక్షత్రం: ${panchang.nakshatramTe} | రాహుకాలం: ${panchang.rahuKalam}"
        } else {
            "Tithi: ${panchang.tithiEn} | Nakshatra: ${panchang.nakshatramEn} | Rahu: ${panchang.rahuKalam}"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PANCHANG_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_MORNING_ID, notification)
    }

    fun showRahuKalamReminder(panchang: PanchangData, language: Language) {
        val isTelugu = language == Language.TELUGU
        val title = if (isTelugu) "⚠️ రాహుకాలం ప్రారంభ హెచ్చరిక" else "⚠️ Rahu Kalam Starts Soon"
        val text = if (isTelugu) {
            "మరో 15 నిమిషాల్లో రాహుకాలం (${panchang.rahuKalam}) ప్రారంభం కానుంది. ముఖ్యమైన పనులను వాయిదా వేసుకోండి."
        } else {
            "Rahu Kalam starts in 15 minutes (${panchang.rahuKalam}). Consider avoiding new ventures."
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_RAHU_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_RAHU_ID, notification)
    }

    fun showUpcomingFestivalNotification(language: Language, city: City) {
        val nextFestival = FestivalCalendar.nextMajor(context, LocalDate.now(), city.latitude, city.longitude) ?: return
        val isTelugu = language == Language.TELUGU
        val title = if (isTelugu) "రేపు విశేష పర్వదినం: ${nextFestival.nameTe}" else "Tomorrow's Festival: ${nextFestival.nameEn}"
        val text = if (isTelugu) {
            "${nextFestival.tithiTe} • ${nextFestival.descriptionTe.take(120)}..."
        } else {
            "${nextFestival.tithiEn} • ${nextFestival.descriptionEn.take(120)}..."
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_FESTIVALS_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_FESTIVAL_ID, notification)
    }

    fun triggerTestNotification(type: String, prefs: UserPreferences, panchang: PanchangData) {
        when (type) {
            "MORNING" -> showMorningPanchangNotification(panchang, prefs.language)
            "RAHU" -> showRahuKalamReminder(panchang, prefs.language)
            "FESTIVAL" -> showUpcomingFestivalNotification(prefs.language, prefs.selectedCity)
        }
    }
}
