package com.example.data.model

import com.example.data.AstronomyEngine
import com.example.data.LunarCalendar
import com.example.data.RealPanchangCalculation
import java.time.LocalDate

data class PanchangData(
    val date: LocalDate,
    val samvatsaramTe: String,
    val samvatsaramEn: String,
    val ayanamTe: String,
    val ayanamEn: String,
    val rutuvuTe: String,
    val rutuvuEn: String,
    val maasamTe: String,
    val maasamEn: String,
    val pakshamTe: String,
    val pakshamEn: String,
    val tithiNumber: Int,
    val tithiTe: String,
    val tithiEn: String,
    val tithiEndTimeTe: String,
    val tithiEndTimeEn: String,
    val vaaramTe: String,
    val vaaramEn: String,
    val nakshatraNumber: Int,
    val nakshatramTe: String,
    val nakshatramEn: String,
    val nakshatraEndTimeTe: String,
    val nakshatraEndTimeEn: String,
    val yogaNumber: Int,
    val yogamTe: String,
    val yogamEn: String,
    val karanaIndex: Int,
    val karanaTe: String,
    val karanaEn: String,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val rahuKalam: String,
    val yamagandam: String,
    val gulikaKalam: String,
    val abhijitMuhurtham: String,
    val durmuhurtham: String,
    val amritKalam: String,
    val isInsideRahuKalam: Boolean,
    val rawCalculation: RealPanchangCalculation? = null
)

object PanchangProvider {
    fun getPanchangForDate(date: LocalDate, city: City): PanchangData {
        val calc = AstronomyEngine.calculatePanchang(
            date = date,
            lat = city.latitude,
            lon = city.longitude
        )
        val lunar = LunarCalendar.getLunarMonth(date, city.latitude, city.longitude)

        return PanchangData(
            date = date,
            samvatsaramTe = calc.samvatsaramTe,
            samvatsaramEn = calc.samvatsaramEn,
            ayanamTe = calc.ayanamTe,
            ayanamEn = calc.ayanamEn,
            rutuvuTe = calc.rutuvuTe,
            rutuvuEn = calc.rutuvuEn,
            maasamTe = "${lunar.month.nameTe} మాసం",
            maasamEn = "${lunar.month.nameEn} Maasam",
            pakshamTe = calc.pakshaTe,
            pakshamEn = calc.pakshaEn,
            tithiNumber = calc.tithiNumber,
            tithiTe = calc.tithiTe,
            tithiEn = calc.tithiEn,
            tithiEndTimeTe = calc.tithiEndTimeStrTe,
            tithiEndTimeEn = calc.tithiEndTimeStrEn,
            vaaramTe = calc.vaaramTe,
            vaaramEn = calc.vaaramEn,
            nakshatraNumber = calc.nakshatraNumber,
            nakshatramTe = calc.nakshatraTe,
            nakshatramEn = calc.nakshatraEn,
            nakshatraEndTimeTe = calc.nakshatraEndTimeStrTe,
            nakshatraEndTimeEn = calc.nakshatraEndTimeStrEn,
            yogaNumber = calc.yogaNumber,
            yogamTe = calc.yogaTe,
            yogamEn = calc.yogaEn,
            karanaIndex = calc.karanaIndex,
            karanaTe = calc.karanaTe,
            karanaEn = calc.karanaEn,
            sunrise = calc.sunMoonTimes.sunriseStr,
            sunset = calc.sunMoonTimes.sunsetStr,
            moonrise = calc.sunMoonTimes.moonriseStr,
            moonset = calc.sunMoonTimes.moonsetStr,
            rahuKalam = calc.rahuKalam.displayStr,
            yamagandam = calc.yamagandam.displayStr,
            gulikaKalam = calc.gulikaKalam.displayStr,
            abhijitMuhurtham = calc.abhijitMuhurtham.displayStr,
            durmuhurtham = calc.yamagandam.displayStr, // classical durmuhurtha representation
            amritKalam = calc.abhijitMuhurtham.displayStr,
            isInsideRahuKalam = calc.isInsideRahuKalam,
            rawCalculation = calc
        )
    }
}
