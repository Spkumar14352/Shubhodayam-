package com.example.data.eclipses

import java.time.LocalTime

/** Traditional timings for grahanam. Change these to match a family's or temple's practice. */
object EclipseConstants {
    /** Sutak (vedha) begins this many hours before a solar eclipse's sparsha. */
    const val SOLAR_SUTAK_HOURS = 12L

    /** Sutak (vedha) begins this many hours before a lunar eclipse's sparsha. */
    const val LUNAR_SUTAK_HOURS = 9L

    /** How far ahead the Grahanam screen lists eclipses. */
    const val LOOKAHEAD_MONTHS = 24L

    /** The Home screen shows a banner when an eclipse starts within this many days. */
    const val HOME_BANNER_DAYS = 7L

    /** Reminder time on the evening before sutak begins. */
    val EVENING_REMINDER_TIME: LocalTime = LocalTime.of(19, 0)

    /** A second reminder this many minutes before sutak begins. */
    const val MINUTES_BEFORE_SUTAK = 30L
}
