package com.example.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

/** Saves an event as an .ics file and hands it to a calendar app. */
object CalendarExport {
    private val IST: ZoneId = ZoneId.of("Asia/Kolkata")
    private val UTC_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
    private val DATE = DateTimeFormatter.ofPattern("yyyyMMdd")

    /** Times are IST; with no [start]/[end] the event is all-day. */
    internal fun ics(
        title: String,
        date: LocalDate,
        start: LocalTime?,
        end: LocalTime?,
        description: String,
        uid: String = UUID.randomUUID().toString()
    ): String {
        fun utc(time: LocalTime) = date.atTime(time).atZone(IST).withZoneSameInstant(ZoneOffset.UTC).format(UTC_STAMP)
        val timing = if (start != null && end != null) {
            listOf("DTSTART:${utc(start)}", "DTEND:${utc(end)}")
        } else {
            listOf("DTSTART;VALUE=DATE:${date.format(DATE)}", "DTEND;VALUE=DATE:${date.plusDays(1).format(DATE)}")
        }
        val lines = listOf(
            "BEGIN:VCALENDAR",
            "VERSION:2.0",
            "PRODID:-//Shubhodaya//Muhurtham//EN",
            "BEGIN:VEVENT",
            "UID:$uid@shubhodaya",
            "DTSTAMP:${java.time.ZonedDateTime.now(ZoneOffset.UTC).format(UTC_STAMP)}"
        ) + timing + listOf(
            "SUMMARY:${escape(title)}",
            "DESCRIPTION:${escape(description)}",
            "END:VEVENT",
            "END:VCALENDAR"
        )
        return lines.joinToString("\r\n", postfix = "\r\n")
    }

    fun open(context: Context, title: String, date: LocalDate, start: LocalTime?, end: LocalTime?, description: String) {
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(dir, "shubhodaya_muhurtham_${date}.ics")
        file.writeText(ics(title, date, start, end, description))
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val view = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "text/calendar")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(Intent.createChooser(view, title))
        } catch (e: ActivityNotFoundException) {
            val send = Intent(Intent.ACTION_SEND)
                .setType("text/calendar")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(Intent.createChooser(send, title))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No calendar app found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun escape(text: String) = text
        .replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace("\n", "\\n")
}
