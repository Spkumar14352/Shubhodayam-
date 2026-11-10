package com.example.data.eclipses

import io.github.cosinekitty.astronomy.Aberration
import io.github.cosinekitty.astronomy.equator
import io.github.cosinekitty.astronomy.horizon
import io.github.cosinekitty.astronomy.nextLocalSolarEclipse
import io.github.cosinekitty.astronomy.nextLunarEclipse
import io.github.cosinekitty.astronomy.searchLocalSolarEclipse
import io.github.cosinekitty.astronomy.searchLunarEclipse
import io.github.cosinekitty.astronomy.Body
import io.github.cosinekitty.astronomy.EclipseKind
import io.github.cosinekitty.astronomy.EquatorEpoch
import io.github.cosinekitty.astronomy.LocalSolarEclipseInfo
import io.github.cosinekitty.astronomy.LunarEclipseInfo
import io.github.cosinekitty.astronomy.Observer
import io.github.cosinekitty.astronomy.Refraction
import io.github.cosinekitty.astronomy.Time
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

enum class EclipseBody { SOLAR, LUNAR }

enum class EclipseType { TOTAL, PARTIAL, ANNULAR, PENUMBRAL }

/**
 * One eclipse as seen from one place. Times are IST. For lunar eclipses [start] and [end] are the
 * umbral contacts (sparsha and moksha); a penumbral eclipse has only penumbral contacts.
 */
data class Eclipse(
    val body: EclipseBody,
    val type: EclipseType,
    val start: LocalDateTime,
    val peak: LocalDateTime,
    val end: LocalDateTime,
    val isVisible: Boolean
) {
    val id: String get() = "${body.name.lowercase()}_${peak.toLocalDate()}"

    val duration: Duration get() = Duration.between(start, end)

    /** Penumbral lunar eclipses are not usually observed traditionally. */
    val isTraditionallyObserved: Boolean get() = type != EclipseType.PENUMBRAL

    /** When sutak begins as per tradition; only for eclipses that are visible here and observed. */
    val sutakStart: LocalDateTime?
        get() = if (!isVisible || !isTraditionallyObserved) {
            null
        } else {
            val hours = if (body == EclipseBody.SOLAR) EclipseConstants.SOLAR_SUTAK_HOURS else EclipseConstants.LUNAR_SUTAK_HOURS
            start.minusHours(hours)
        }

    val nameEn: String get() {
        val kind = when (type) {
            EclipseType.TOTAL -> "Total"
            EclipseType.PARTIAL -> "Partial"
            EclipseType.ANNULAR -> "Annular"
            EclipseType.PENUMBRAL -> "Penumbral"
        }
        return "$kind ${if (body == EclipseBody.SOLAR) "Solar" else "Lunar"} Eclipse"
    }

    val nameTe: String get() {
        val kind = when (type) {
            EclipseType.TOTAL -> "సంపూర్ణ"
            EclipseType.PARTIAL -> "పాక్షిక"
            EclipseType.ANNULAR -> "కంకణ"
            EclipseType.PENUMBRAL -> "ఉపచ్ఛాయ"
        }
        return "$kind ${if (body == EclipseBody.SOLAR) "సూర్య" else "చంద్ర"} గ్రహణం"
    }
}

/** Eclipse predictions from Astronomy Engine, for an observer at the user's city. */
object EclipseCalculator {
    val IST: ZoneId = ZoneId.of("Asia/Kolkata")
    private const val VISIBILITY_STEP_MINUTES = 5.0
    private const val MINUTES_PER_DAY = 1440.0

    fun upcoming(
        from: Instant,
        lat: Double,
        lon: Double,
        months: Long = EclipseConstants.LOOKAHEAD_MONTHS
    ): List<Eclipse> {
        val observer = Observer(lat, lon, 0.0)
        val until = from.atZone(IST).plusMonths(months).toInstant()
        // Start a day early so an eclipse already in progress is included.
        val searchStart = Time.fromMillisecondsSince1970(from.minus(Duration.ofDays(1)).toEpochMilli())

        val eclipses = mutableListOf<Eclipse>()

        var lunar = searchLunarEclipse(searchStart)
        while (instant(lunar.peak) < until) {
            eclipses += lunarEclipse(lunar, observer)
            lunar = nextLunarEclipse(lunar.peak)
        }

        var solar = searchLocalSolarEclipse(searchStart, observer)
        while (instant(solar.peak.time) < until) {
            eclipses += solarEclipse(solar)
            solar = nextLocalSolarEclipse(solar.peak.time, observer)
        }

        val now = LocalDateTime.ofInstant(from, IST)
        return eclipses.filter { it.end > now }.sortedBy { it.start }
    }

    private fun lunarEclipse(info: LunarEclipseInfo, observer: Observer): Eclipse {
        val type = type(info.kind)
        val semiDurationMinutes = if (type == EclipseType.PENUMBRAL) info.sdPenum else info.sdPartial
        val start = info.peak.addDays(-semiDurationMinutes / MINUTES_PER_DAY)
        val end = info.peak.addDays(semiDurationMinutes / MINUTES_PER_DAY)
        return Eclipse(
            body = EclipseBody.LUNAR,
            type = type,
            start = ist(start),
            peak = ist(info.peak),
            end = ist(end),
            isVisible = moonAboveHorizonDuring(start, end, observer)
        )
    }

    private fun solarEclipse(info: LocalSolarEclipseInfo) = Eclipse(
        body = EclipseBody.SOLAR,
        type = type(info.kind),
        start = ist(info.partialBegin.time),
        peak = ist(info.peak.time),
        end = ist(info.partialEnd.time),
        isVisible = listOf(info.partialBegin, info.peak, info.partialEnd).any { it.altitude > 0 }
    )

    private fun moonAboveHorizonDuring(start: Time, end: Time, observer: Observer): Boolean {
        var time = start
        while (time < end) {
            if (moonAltitude(time, observer) > 0) return true
            time = time.addDays(VISIBILITY_STEP_MINUTES / MINUTES_PER_DAY)
        }
        return moonAltitude(end, observer) > 0
    }

    private fun moonAltitude(time: Time, observer: Observer): Double {
        val equatorial = equator(Body.Moon, time, observer, EquatorEpoch.OfDate, Aberration.Corrected)
        return horizon(time, observer, equatorial.ra, equatorial.dec, Refraction.Normal).altitude
    }

    private fun type(kind: EclipseKind) = when (kind) {
        EclipseKind.Total -> EclipseType.TOTAL
        EclipseKind.Partial -> EclipseType.PARTIAL
        EclipseKind.Annular -> EclipseType.ANNULAR
        EclipseKind.Penumbral -> EclipseType.PENUMBRAL
    }

    private fun instant(time: Time): Instant = Instant.ofEpochMilli(time.toMillisecondsSince1970())

    private fun ist(time: Time): LocalDateTime = LocalDateTime.ofInstant(instant(time), IST)
}
