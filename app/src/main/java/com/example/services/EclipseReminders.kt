package com.example.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.eclipses.Eclipse
import com.example.data.eclipses.EclipseCalculator
import com.example.data.eclipses.EclipseConstants
import com.example.data.model.Language
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Two reminders per observed eclipse: the evening before sutak begins and shortly before it.
 * Alarms don't survive a reboot; they are set again whenever the app opens.
 */
object EclipseReminders {
    internal const val CHANNEL_ID = "shubhodaya_grahanam_channel"
    internal const val EXTRA_TITLE = "title"
    internal const val EXTRA_TEXT = "text"
    internal const val EXTRA_ID = "notification_id"
    private const val REQUEST_CODE_BASE = 7000
    private const val MAX_REMINDERS = 12

    data class Reminder(val at: LocalDateTime, val title: String, val text: String)

    fun schedule(context: Context, eclipses: List<Eclipse>, language: Language, enabled: Boolean) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val reminders = if (enabled) remindersFor(eclipses, language, LocalDateTime.now(EclipseCalculator.IST)) else emptyList()

        // Fixed request codes, so a reschedule replaces earlier alarms and clears unused ones.
        for (slot in 0 until MAX_REMINDERS) {
            val reminder = reminders.getOrNull(slot)
            val intent = Intent(context, EclipseReminderReceiver::class.java)
            if (reminder != null) {
                intent.putExtra(EXTRA_TITLE, reminder.title)
                    .putExtra(EXTRA_TEXT, reminder.text)
                    .putExtra(EXTRA_ID, REQUEST_CODE_BASE + slot)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_BASE + slot,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if (reminder == null) {
                alarmManager.cancel(pending)
            } else {
                val millis = reminder.at.atZone(EclipseCalculator.IST).toInstant().toEpochMilli()
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pending)
            }
        }
    }

    internal fun remindersFor(eclipses: List<Eclipse>, language: Language, now: LocalDateTime): List<Reminder> {
        val isTelugu = language == Language.TELUGU
        val time = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        return eclipses.flatMap { eclipse ->
            val sutak = eclipse.sutakStart ?: return@flatMap emptyList()
            val name = if (isTelugu) eclipse.nameTe else eclipse.nameEn
            val at = sutak.format(time)
            listOf(
                Reminder(
                    at = sutak.toLocalDate().minusDays(1).atTime(EclipseConstants.EVENING_REMINDER_TIME),
                    title = if (isTelugu) "రేపు $name" else "Tomorrow: $name",
                    text = if (isTelugu) "సంప్రదాయం ప్రకారం సూతకం రేపు $at నుండి ప్రారంభం." else "As per tradition, sutak begins tomorrow at $at."
                ),
                Reminder(
                    at = sutak.minusMinutes(EclipseConstants.MINUTES_BEFORE_SUTAK),
                    title = name,
                    text = if (isTelugu) {
                        "సంప్రదాయం ప్రకారం సూతకం ${EclipseConstants.MINUTES_BEFORE_SUTAK} నిమిషాల్లో ($at) ప్రారంభం."
                    } else {
                        "As per tradition, sutak begins in ${EclipseConstants.MINUTES_BEFORE_SUTAK} minutes ($at)."
                    }
                )
            )
        }.filter { it.at > now }.sortedBy { it.at }.take(MAX_REMINDERS)
    }
}

class EclipseReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EclipseReminders.EXTRA_TITLE) ?: return
        val text = intent.getStringExtra(EclipseReminders.EXTRA_TEXT).orEmpty()
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    EclipseReminders.CHANNEL_ID,
                    "Grahanam Reminders (గ్రహణ సమాచారం)",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, EclipseReminders.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        try {
            manager.notify(intent.getIntExtra(EclipseReminders.EXTRA_ID, 0), notification)
        } catch (e: SecurityException) {
            // The notification permission was revoked after the check above.
        }
    }
}
