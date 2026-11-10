package com.example.data.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** One festival on one date. Dates come from [com.example.data.festivals.FestivalCalendar]. */
data class FestivalCalendarItem(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val dateString: String, // YYYY-MM-DD
    val monthGroupEn: String,
    val monthGroupTe: String,
    val tithiEn: String,
    val tithiTe: String,
    val descriptionEn: String,
    val descriptionTe: String,
    val ritualsEn: String,
    val ritualsTe: String,
    /** The rule id from /data (e.g. "ugadi"), shared by every year's occurrence. */
    val baseId: String = id,
    /** False for recurring observances such as Ekadashi and monthly sankrantis. */
    val isMajor: Boolean = true,
    /** True when the date comes from /data/festivalOverrides.ts. */
    val isVerified: Boolean = false,
    val noteEn: String = "",
    val noteTe: String = ""
) {
    val date: LocalDate by lazy { LocalDate.parse(dateString) }

    fun getDaysLeft(fromDate: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(fromDate, date)
    }
}
