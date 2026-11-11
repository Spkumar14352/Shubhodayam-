package com.example.data.festivals

import android.content.Context
import com.example.data.model.FestivalCalendarItem
import java.time.LocalDate
import kotlin.math.roundToInt

/** Calculated festivals for the user's location, cached per year and place. */
object FestivalCalendar {
    private var calculator: FestivalCalculator? = null
    private val cache = mutableMapOf<Triple<Int, Int, Int>, List<FestivalCalendarItem>>()

    @Synchronized
    fun forYear(context: Context, year: Int, lat: Double, lon: Double): List<FestivalCalendarItem> {
        val calc = calculator ?: FestivalCalculator(FestivalData.fromAssets(context.applicationContext)).also { calculator = it }
        // Sunrise barely moves within ~1 km, so nearby coordinates share a result.
        val key = Triple(year, (lat * 100).roundToInt(), (lon * 100).roundToInt())
        return cache.getOrPut(key) { calc.calculate(year, lat, lon) }
    }

    /** Festivals on [date], major ones first. */
    fun on(context: Context, date: LocalDate, lat: Double, lon: Double): List<FestivalCalendarItem> =
        forYear(context, date.year, lat, lon).filter { it.date == date }

    fun upcoming(context: Context, from: LocalDate, lat: Double, lon: Double): List<FestivalCalendarItem> =
        (forYear(context, from.year, lat, lon) + forYear(context, from.year + 1, lat, lon))
            .filter { !it.date.isBefore(from) }

    fun nextMajor(context: Context, from: LocalDate, lat: Double, lon: Double): FestivalCalendarItem? =
        upcoming(context, from, lat, lon).firstOrNull { it.isMajor }

    /** Rolling 14 months of festivals starting from the 1st of [fromDate]'s month. */
    fun forRolling14Months(context: Context, fromDate: LocalDate, lat: Double, lon: Double): List<FestivalCalendarItem> {
        val startMonth = fromDate.withDayOfMonth(1)
        val endLimit = startMonth.plusMonths(14)
        val years = (startMonth.year..endLimit.year).toList()
        return years.flatMap { forYear(context, it, lat, lon) }
            .filter { !it.date.isBefore(startMonth) && it.date.isBefore(endLimit) }
            .sortedWith(compareBy<FestivalCalendarItem> { it.date }.thenBy { !it.isMajor })
    }
}
