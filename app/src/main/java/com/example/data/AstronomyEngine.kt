package com.example.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class CelestialPositions(
    val tropicalSun: Double,
    val tropicalMoon: Double,
    val siderealSun: Double,
    val siderealMoon: Double,
    val ayanamsa: Double
)

data class SunMoonTimes(
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val moonrise: LocalTime,
    val moonset: LocalTime,
    val sunriseStr: String,
    val sunsetStr: String,
    val moonriseStr: String,
    val moonsetStr: String
)

data class MuhurthamPeriod(
    val nameTe: String,
    val nameEn: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val displayStr: String,
    val isCurrentActive: Boolean = false
)

data class RealPanchangCalculation(
    val date: LocalDate,
    val cityLatitude: Double,
    val cityLongitude: Double,
    val vaaramTe: String,
    val vaaramEn: String,
    val tithiNumber: Int,
    val tithiTe: String,
    val tithiEn: String,
    val tithiEndTimeStrTe: String,
    val tithiEndTimeStrEn: String,
    val tithiEndDateTime: LocalDateTime,
    val pakshaTe: String,
    val pakshaEn: String,
    val nakshatraNumber: Int,
    val nakshatraTe: String,
    val nakshatraEn: String,
    val nakshatraEndTimeStrTe: String,
    val nakshatraEndTimeStrEn: String,
    val nakshatraEndDateTime: LocalDateTime,
    val yogaNumber: Int,
    val yogaTe: String,
    val yogaEn: String,
    val karanaIndex: Int,
    val karanaTe: String,
    val karanaEn: String,
    val sunMoonTimes: SunMoonTimes,
    val rahuKalam: MuhurthamPeriod,
    val yamagandam: MuhurthamPeriod,
    val gulikaKalam: MuhurthamPeriod,
    val abhijitMuhurtham: MuhurthamPeriod,
    val isInsideRahuKalam: Boolean,
    val samvatsaramTe: String = "శ్రీ క్రోధి నామ సంవత్సరం",
    val samvatsaramEn: String = "Sri Krodhi Nama Samvatsaram",
    val ayanamTe: String = "దక్షిణాయనం",
    val ayanamEn: String = "Dakshinayanam",
    val rutuvuTe: String = "శరద్ ఋతువు",
    val rutuvuEn: String = "Sharad Ruthuvu",
    val maasamTe: String = "కార్తీక మాసం",
    val maasamEn: String = "Karthika Maasam"
)

object AstronomyEngine {
    val KOLKATA_ZONE = ZoneId.of("Asia/Kolkata")

    private fun degToRad(deg: Double): Double = deg * PI / 180.0
    private fun radToDeg(rad: Double): Double = rad * 180.0 / PI

    private fun normalizeDegrees(deg: Double): Double {
        val rem = deg % 360.0
        return if (rem < 0) rem + 360.0 else rem
    }

    /**
     * Compute Julian Day from UTC LocalDateTime
     */
    fun toJulianDay(utcDateTime: LocalDateTime): Double {
        var year = utcDateTime.year
        var month = utcDateTime.monthValue
        val day = utcDateTime.dayOfMonth
        val hour = utcDateTime.hour + utcDateTime.minute / 60.0 + utcDateTime.second / 3600.0

        if (month <= 2) {
            year -= 1
            month += 12
        }

        val a = floor(year / 100.0)
        val b = 2 - a + floor(a / 4.0)

        return floor(365.25 * (year + 4716)) +
                floor(30.6001 * (month + 1)) +
                day + hour / 24.0 + b - 1524.5
    }

    /**
     * Get Sun & Moon ecliptic longitudes (Tropical and Lahiri Sidereal)
     */
    fun getCelestialPositions(kolkataDateTime: LocalDateTime): CelestialPositions {
        // Convert Asia/Kolkata (+5:30) to UTC
        val utcDateTime = kolkataDateTime.minusHours(5).minusMinutes(30)
        val jd = toJulianDay(utcDateTime)
        val t = (jd - 2451545.0) / 36525.0

        // Sun's Tropical Longitude (Meeus Chapter 25)
        val l0 = normalizeDegrees(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val mSun = normalizeDegrees(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mSunRad = degToRad(mSun)
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mSunRad) +
                (0.019993 - 0.000101 * t) * sin(2 * mSunRad) +
                0.000289 * sin(3 * mSunRad)
        val trueSunLong = normalizeDegrees(l0 + c)

        // Moon's Tropical Longitude (Meeus Chapter 47)
        val lPrime = normalizeDegrees(218.3164477 + 481267.88123421 * t - 0.0015786 * t * t)
        val d = normalizeDegrees(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t)
        val mMoon = normalizeDegrees(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t)
        val f = normalizeDegrees(93.2720950 + 483202.0175233 * t - 0.0036539 * t * t)

        val dRad = degToRad(d)
        val mSunR = degToRad(mSun)
        val mMoonR = degToRad(mMoon)
        val fRad = degToRad(f)

        val moonPerturbation = 6.288774 * sin(mMoonR) +
                1.274027 * sin(2 * dRad - mMoonR) +
                0.658314 * sin(2 * dRad) +
                0.213618 * sin(2 * mMoonR) -
                0.185116 * sin(mSunR) -
                0.114332 * sin(2 * fRad) +
                0.058793 * sin(2 * dRad - 2 * mMoonR) +
                0.057066 * sin(2 * dRad - mSunR - mMoonR) +
                0.053322 * sin(2 * dRad + mMoonR) +
                0.046193 * sin(2 * dRad - mSunR) -
                0.034728 * sin(dRad) -
                0.030383 * sin(mSunR + mMoonR)

        val trueMoonLong = normalizeDegrees(lPrime + moonPerturbation)

        // Lahiri Ayanamsa: 23.853 + (year - 2000) * 0.013969 degrees
        val decimalYear = kolkataDateTime.year + (kolkataDateTime.dayOfYear / 365.25)
        val ayanamsa = 23.853 + (decimalYear - 2000.0) * 0.013969

        val siderealSun = normalizeDegrees(trueSunLong - ayanamsa)
        val siderealMoon = normalizeDegrees(trueMoonLong - ayanamsa)

        return CelestialPositions(
            tropicalSun = trueSunLong,
            tropicalMoon = trueMoonLong,
            siderealSun = siderealSun,
            siderealMoon = siderealMoon,
            ayanamsa = ayanamsa
        )
    }

    fun getTithiNumber(siderealMoon: Double, siderealSun: Double): Int {
        val diff = normalizeDegrees(siderealMoon - siderealSun)
        return floor(diff / 12.0).toInt() + 1 // 1..30
    }

    fun getNakshatraNumber(siderealMoon: Double): Int {
        val span = 360.0 / 27.0 // 13.333333 degrees
        return floor(siderealMoon / span).toInt() + 1 // 1..27
    }

    fun getYogaNumber(siderealSun: Double, siderealMoon: Double): Int {
        val span = 360.0 / 27.0
        val sum = normalizeDegrees(siderealSun + siderealMoon)
        return floor(sum / span).toInt() + 1 // 1..27
    }

    fun getKaranaIndex(siderealMoon: Double, siderealSun: Double): Int {
        val diff = normalizeDegrees(siderealMoon - siderealSun)
        return floor(diff / 6.0).toInt() // 0..59
    }

    /**
     * Compute Sunrise, Sunset, Moonrise, Moonset for user's lat/long on date (Asia/Kolkata)
     */
    fun computeSunMoonTimes(date: LocalDate, lat: Double, lon: Double): SunMoonTimes {
        val dayOfYear = date.dayOfYear
        val b = 2.0 * PI * (dayOfYear - 81) / 365.0
        val eqTimeMinutes = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b)
        val solarDeclination = 23.45 * sin(b)

        val latRad = degToRad(lat)
        val decRad = degToRad(solarDeclination)
        val zenith = degToRad(90.8333) // atmospheric refraction standard

        val cosHourAngle = (cos(zenith) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clampedCos = cosHourAngle.coerceIn(-1.0, 1.0)
        val hourAngleDeg = radToDeg(acos(clampedCos))

        val timeOffsetMinutes = (lon - 82.5) * 4.0 // Standard Indian Meridian is 82.5° E
        val solarNoonMinutes = 12 * 60 - timeOffsetMinutes - eqTimeMinutes

        val sunriseMinutes = (solarNoonMinutes - hourAngleDeg * 4.0).toInt()
        val sunsetMinutes = (solarNoonMinutes + hourAngleDeg * 4.0).toInt()

        val riseH = (sunriseMinutes / 60).coerceIn(0, 23)
        val riseM = (sunriseMinutes % 60).coerceIn(0, 59)
        val setH = (sunsetMinutes / 60).coerceIn(0, 23)
        val setM = (sunsetMinutes % 60).coerceIn(0, 59)

        val sunrise = LocalTime.of(riseH, riseM)
        val sunset = LocalTime.of(setH, setM)

        // Moon times approximation
        val moonLagMinutes = ((date.dayOfMonth * 48) % (24 * 60))
        val moonriseMinutes = (sunriseMinutes + moonLagMinutes) % (24 * 60)
        val moonsetMinutes = (sunsetMinutes + moonLagMinutes) % (24 * 60)

        val mRise = LocalTime.of((moonriseMinutes / 60) % 24, moonriseMinutes % 60)
        val mSet = LocalTime.of((moonsetMinutes / 60) % 24, moonsetMinutes % 60)

        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

        return SunMoonTimes(
            sunrise = sunrise,
            sunset = sunset,
            moonrise = mRise,
            moonset = mSet,
            sunriseStr = sunrise.format(formatter),
            sunsetStr = sunset.format(formatter),
            moonriseStr = mRise.format(formatter),
            moonsetStr = mSet.format(formatter)
        )
    }

    /**
     * Compute the end time of current Tithi and Nakshatra by stepping forward
     * in 10-minute increments, then refining to the minute.
     */
    fun findTithiEndTime(
        date: LocalDate,
        initialTithi: Int,
        startTime: LocalDateTime
    ): Pair<LocalDateTime, String> {
        var cursor = startTime
        val maxCursor = startTime.plusHours(36) // limit search window to 36 hours

        // Step 10 minutes forward
        while (cursor.isBefore(maxCursor)) {
            val nextCursor = cursor.plusMinutes(10)
            val pos = getCelestialPositions(nextCursor)
            val tNum = getTithiNumber(pos.siderealMoon, pos.siderealSun)
            if (tNum != initialTithi) {
                // Refine to the minute
                var minuteCursor = cursor
                while (minuteCursor.isBefore(nextCursor)) {
                    val p = getCelestialPositions(minuteCursor)
                    if (getTithiNumber(p.siderealMoon, p.siderealSun) != initialTithi) {
                        return Pair(minuteCursor, formatEndTime(date, minuteCursor))
                    }
                    minuteCursor = minuteCursor.plusMinutes(1)
                }
                return Pair(nextCursor, formatEndTime(date, nextCursor))
            }
            cursor = nextCursor
        }

        return Pair(cursor, formatEndTime(date, cursor))
    }

    fun findNakshatraEndTime(
        date: LocalDate,
        initialNakshatra: Int,
        startTime: LocalDateTime
    ): Pair<LocalDateTime, String> {
        var cursor = startTime
        val maxCursor = startTime.plusHours(36)

        while (cursor.isBefore(maxCursor)) {
            val nextCursor = cursor.plusMinutes(10)
            val pos = getCelestialPositions(nextCursor)
            val nNum = getNakshatraNumber(pos.siderealMoon)
            if (nNum != initialNakshatra) {
                var minuteCursor = cursor
                while (minuteCursor.isBefore(nextCursor)) {
                    val p = getCelestialPositions(minuteCursor)
                    if (getNakshatraNumber(p.siderealMoon) != initialNakshatra) {
                        return Pair(minuteCursor, formatEndTime(date, minuteCursor))
                    }
                    minuteCursor = minuteCursor.plusMinutes(1)
                }
                return Pair(nextCursor, formatEndTime(date, nextCursor))
            }
            cursor = nextCursor
        }

        return Pair(cursor, formatEndTime(date, cursor))
    }

    private fun formatEndTime(baseDate: LocalDate, targetDateTime: LocalDateTime): String {
        val timeFmt = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        val formattedTime = targetDateTime.toLocalTime().format(timeFmt)
        return if (targetDateTime.toLocalDate().isAfter(baseDate)) {
            "రేపు $formattedTime (Next day)"
        } else {
            formattedTime
        }
    }

    /**
     * Calculate comprehensive Panchang for a date and location
     */
    fun calculatePanchang(date: LocalDate, lat: Double, lon: Double): RealPanchangCalculation {
        val sunMoon = computeSunMoonTimes(date, lat, lon)
        val sunriseDateTime = LocalDateTime.of(date, sunMoon.sunrise)

        // Celestial positions at sunrise
        val positionsAtSunrise = getCelestialPositions(sunriseDateTime)
        val tithiNum = getTithiNumber(positionsAtSunrise.siderealMoon, positionsAtSunrise.siderealSun)
        val nakshatraNum = getNakshatraNumber(positionsAtSunrise.siderealMoon)
        val yogaNum = getYogaNumber(positionsAtSunrise.siderealSun, positionsAtSunrise.siderealMoon)
        val karanaIdx = getKaranaIndex(positionsAtSunrise.siderealMoon, positionsAtSunrise.siderealSun)

        // Tithi and Nakshatra end times
        val (tithiEndAt, tithiEndStr) = findTithiEndTime(date, tithiNum, sunriseDateTime)
        val (nakshatraEndAt, nakshatraEndStr) = findNakshatraEndTime(date, nakshatraNum, sunriseDateTime)

        val tithiEntry = PanchangNames.getTithi(tithiNum)
        val nakshatraEntry = PanchangNames.getNakshatra(nakshatraNum)
        val yogaEntry = PanchangNames.getYoga(yogaNum)
        val karanaEntry = PanchangNames.getKarana(karanaIdx)

        val pakshaTe = if (tithiNum <= 15) "శుక్ల పక్షం" else "కృష్ణ పక్షం"
        val pakshaEn = if (tithiNum <= 15) "Shukla Paksha" else "Krishna Paksha"

        val dayOfWeek = date.dayOfWeek.value // 1 (Mon) - 7 (Sun)
        val (vaaramTe, vaaramEn) = when (dayOfWeek) {
            1 -> Pair("సోమవారం", "Monday (Somavaram)")
            2 -> Pair("మంగళవారం", "Tuesday (Mangalavaram)")
            3 -> Pair("బుధవారం", "Wednesday (Budhavaram)")
            4 -> Pair("గురువారం", "Thursday (Guruvaram)")
            5 -> Pair("శుక్రవారం", "Friday (Sukravaram)")
            6 -> Pair("శనివారం", "Saturday (Sanivaram)")
            else -> Pair("ఆదివారం", "Sunday (Aadivaram)")
        }

        // Daytime 8 equal parts for Rahu, Yama, Gulika
        val sunriseMinutes = sunMoon.sunrise.hour * 60 + sunMoon.sunrise.minute
        val sunsetMinutes = sunMoon.sunset.hour * 60 + sunMoon.sunset.minute
        val dayDurationMinutes = (sunsetMinutes - sunriseMinutes).toDouble()
        val partDuration = dayDurationMinutes / 8.0

        // Day of week index (Sunday = 0, Monday = 1, ..., Saturday = 6)
        val dowIndex = if (dayOfWeek == 7) 0 else dayOfWeek

        // Rahu Kalam part (1-indexed)
        // Sun 8, Mon 2, Tue 7, Wed 5, Thu 6, Fri 4, Sat 3
        val rahuPart = when (dowIndex) {
            0 -> 8
            1 -> 2
            2 -> 7
            3 -> 5
            4 -> 6
            5 -> 4
            else -> 3
        }

        // Yamagandam part
        // Sun 5, Mon 4, Tue 3, Wed 2, Thu 1, Fri 7, Sat 6
        val yamaPart = when (dowIndex) {
            0 -> 5
            1 -> 4
            2 -> 3
            3 -> 2
            4 -> 1
            5 -> 7
            else -> 6
        }

        // Gulika Kalam part
        // Sun 7, Mon 6, Tue 5, Wed 4, Thu 3, Fri 2, Sat 1
        val gulikaPart = when (dowIndex) {
            0 -> 7
            1 -> 6
            2 -> 5
            3 -> 4
            4 -> 3
            5 -> 2
            else -> 1
        }

        fun getPeriod(part: Int, nameTe: String, nameEn: String): MuhurthamPeriod {
            val startM = (sunriseMinutes + (part - 1) * partDuration).toInt()
            val endM = (sunriseMinutes + part * partDuration).toInt()
            val st = LocalTime.of((startM / 60) % 24, startM % 60)
            val et = LocalTime.of((endM / 60) % 24, endM % 60)
            val fmt = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
            return MuhurthamPeriod(
                nameTe = nameTe,
                nameEn = nameEn,
                startTime = st,
                endTime = et,
                displayStr = "${st.format(fmt)} - ${et.format(fmt)}"
            )
        }

        val rahuPeriod = getPeriod(rahuPart, "రాహుకాలం", "Rahu Kalam")
        val yamaPeriod = getPeriod(yamaPart, "యమగండం", "Yamagandam")
        val gulikaPeriod = getPeriod(gulikaPart, "గుళికా కాలం", "Gulika Kalam")

        // Abhijit Muhurtham: the 8th of 15 equal daytime parts (around midday)
        val muhurthaDuration = dayDurationMinutes / 15.0
        val abhijitStartM = (sunriseMinutes + 7 * muhurthaDuration).toInt()
        val abhijitEndM = (sunriseMinutes + 8 * muhurthaDuration).toInt()
        val abhijitStart = LocalTime.of((abhijitStartM / 60) % 24, abhijitStartM % 60)
        val abhijitEnd = LocalTime.of((abhijitEndM / 60) % 24, abhijitEndM % 60)
        val timeFmt = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        val abhijitPeriod = MuhurthamPeriod(
            nameTe = "అభిజిత్ ముహూర్తం",
            nameEn = "Abhijit Muhurtham",
            startTime = abhijitStart,
            endTime = abhijitEnd,
            displayStr = "${abhijitStart.format(timeFmt)} - ${abhijitEnd.format(timeFmt)}"
        )

        // Check if current time is inside Rahu Kalam
        val nowKolkata = LocalTime.now(KOLKATA_ZONE)
        val isToday = date == LocalDate.now(KOLKATA_ZONE)
        val isInsideRahu = isToday && nowKolkata.isAfter(rahuPeriod.startTime) && nowKolkata.isBefore(rahuPeriod.endTime)

        return RealPanchangCalculation(
            date = date,
            cityLatitude = lat,
            cityLongitude = lon,
            vaaramTe = vaaramTe,
            vaaramEn = vaaramEn,
            tithiNumber = tithiNum,
            tithiTe = tithiEntry.nameTe,
            tithiEn = tithiEntry.nameEn,
            tithiEndTimeStrTe = "రాత్రి/పగలు $tithiEndStr వరకు",
            tithiEndTimeStrEn = "ends by $tithiEndStr",
            tithiEndDateTime = tithiEndAt,
            pakshaTe = pakshaTe,
            pakshaEn = pakshaEn,
            nakshatraNumber = nakshatraNum,
            nakshatraTe = nakshatraEntry.nameTe,
            nakshatraEn = nakshatraEntry.nameEn,
            nakshatraEndTimeStrTe = "వరకు $nakshatraEndStr",
            nakshatraEndTimeStrEn = "ends by $nakshatraEndStr",
            nakshatraEndDateTime = nakshatraEndAt,
            yogaNumber = yogaNum,
            yogaTe = yogaEntry.nameTe,
            yogaEn = yogaEntry.nameEn,
            karanaIndex = karanaIdx,
            karanaTe = karanaEntry.nameTe,
            karanaEn = karanaEntry.nameEn,
            sunMoonTimes = sunMoon,
            rahuKalam = rahuPeriod,
            yamagandam = yamaPeriod,
            gulikaKalam = gulikaPeriod,
            abhijitMuhurtham = abhijitPeriod,
            isInsideRahuKalam = isInsideRahu
        )
    }
}
