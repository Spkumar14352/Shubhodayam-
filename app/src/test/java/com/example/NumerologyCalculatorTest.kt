package com.example

import com.example.data.numerology.NumerologyCalculator
import com.example.data.numerology.NumerologyData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NumerologyCalculatorTest {

    @Test
    fun testMoolankCalculations() {
        // E.g. DOB 29-08-1990 -> Moolank 2 (29 -> 2+9 = 11 -> 1+1 = 2)
        assertEquals(2, NumerologyCalculator.calculateMoolank(29))

        // Single digit days
        assertEquals(1, NumerologyCalculator.calculateMoolank(1))
        assertEquals(5, NumerologyCalculator.calculateMoolank(5))
        assertEquals(9, NumerologyCalculator.calculateMoolank(9))

        // Compound days
        assertEquals(1, NumerologyCalculator.calculateMoolank(10)) // 1+0 = 1
        assertEquals(2, NumerologyCalculator.calculateMoolank(11)) // 1+1 = 2
        assertEquals(3, NumerologyCalculator.calculateMoolank(12)) // 1+2 = 3
        assertEquals(9, NumerologyCalculator.calculateMoolank(18)) // 1+8 = 9
        assertEquals(1, NumerologyCalculator.calculateMoolank(19)) // 1+9 = 10 -> 1
        assertEquals(2, NumerologyCalculator.calculateMoolank(20)) // 2+0 = 2
        assertEquals(9, NumerologyCalculator.calculateMoolank(27)) // 2+7 = 9
        assertEquals(1, NumerologyCalculator.calculateMoolank(28)) // 2+8 = 10 -> 1
        assertEquals(4, NumerologyCalculator.calculateMoolank(31)) // 3+1 = 4
    }

    @Test
    fun testBhagyankCalculations() {
        // DOB 29-08-1990:
        // Day digits: 2+9 = 11
        // Month digits: 0+8 = 8
        // Year digits: 1+9+9+0 = 19
        // Total sum = 2+9+0+8+1+9+9+0 = 38 -> 3+8 = 11 (Master 11) -> 1+1 = 2
        val dob = LocalDate.of(1990, 8, 29)
        val result = NumerologyCalculator.calculateBhagyank(dob)

        assertEquals(2, result.singleDigit)
        assertEquals(11, result.masterNumber)
        assertEquals(38, result.compoundTotal)

        // Test date with Master 22
        // e.g., 29-09-1992 -> 2+9+0+9+1+9+9+2 = 40 -> 4
        // e.g., 22-04-1977 -> 2+2+0+4+1+9+7+7 = 32 -> 5
        // e.g., 18-02-1920 -> 1+8+0+2+1+9+2+0 = 23 -> 5
        // Let's test a date summing to 22: 04-09-1980 -> 0+4+0+9+1+9+8+0 = 31 -> 4
        // Date summing to 22: 11-02-1990 -> 1+1+0+2+1+9+9+0 = 23 -> 5
        // Date summing to 22: 11-01-1999 -> 1+1+0+1+1+9+9+9 = 31 -> 4
        // Date summing to 22: 09-02-1928 -> 0+9+0+2+1+9+2+8 = 31 -> 4
        // Date summing directly to 22: 02-02-1980 -> 2+2+1+9+8+0 = 22 -> Master 22, singleDigit 4
        val dob22 = LocalDate.of(1980, 2, 2)
        val res22 = NumerologyCalculator.calculateBhagyank(dob22)
        assertEquals(4, res22.singleDigit)
        assertEquals(22, res22.masterNumber)
        assertEquals(22, res22.compoundTotal)
    }

    @Test
    fun testChaldeanNameNumberCalculations() {
        // Name "RAVI" -> R(2) + A(1) + V(6) + I(1) = 10 -> 1+0 = 1
        val raviResult = NumerologyCalculator.calculateNameNumber("RAVI")
        assertEquals(10, raviResult.compoundTotal)
        assertEquals(1, raviResult.singleDigit)

        // Ignore spaces, lowercase, dots, numbers: "Ravi. 123"
        val raviWithPunct = NumerologyCalculator.calculateNameNumber("Ravi. 123")
        assertEquals(10, raviWithPunct.compoundTotal)
        assertEquals(1, raviWithPunct.singleDigit)

        // Name "SHIVA" -> S(3) + H(5) + I(1) + V(6) + A(1) = 16 -> 1+6 = 7
        val shivaResult = NumerologyCalculator.calculateNameNumber("SHIVA")
        assertEquals(16, shivaResult.compoundTotal)
        assertEquals(7, shivaResult.singleDigit)

        // Check full Chaldean values mapping
        assertEquals(1, NumerologyCalculator.CHALDEAN_MAP['A'])
        assertEquals(2, NumerologyCalculator.CHALDEAN_MAP['B'])
        assertEquals(3, NumerologyCalculator.CHALDEAN_MAP['C'])
        assertEquals(4, NumerologyCalculator.CHALDEAN_MAP['D'])
        assertEquals(5, NumerologyCalculator.CHALDEAN_MAP['E'])
        assertEquals(8, NumerologyCalculator.CHALDEAN_MAP['F'])
        assertEquals(3, NumerologyCalculator.CHALDEAN_MAP['G'])
        assertEquals(5, NumerologyCalculator.CHALDEAN_MAP['H'])
        assertEquals(1, NumerologyCalculator.CHALDEAN_MAP['I'])
        assertEquals(1, NumerologyCalculator.CHALDEAN_MAP['J'])
        assertEquals(2, NumerologyCalculator.CHALDEAN_MAP['K'])
        assertEquals(3, NumerologyCalculator.CHALDEAN_MAP['L'])
        assertEquals(4, NumerologyCalculator.CHALDEAN_MAP['M'])
        assertEquals(5, NumerologyCalculator.CHALDEAN_MAP['N'])
        assertEquals(7, NumerologyCalculator.CHALDEAN_MAP['O'])
        assertEquals(8, NumerologyCalculator.CHALDEAN_MAP['P'])
        assertEquals(1, NumerologyCalculator.CHALDEAN_MAP['Q'])
        assertEquals(2, NumerologyCalculator.CHALDEAN_MAP['R'])
        assertEquals(3, NumerologyCalculator.CHALDEAN_MAP['S'])
        assertEquals(4, NumerologyCalculator.CHALDEAN_MAP['T'])
        assertEquals(6, NumerologyCalculator.CHALDEAN_MAP['U'])
        assertEquals(6, NumerologyCalculator.CHALDEAN_MAP['V'])
        assertEquals(6, NumerologyCalculator.CHALDEAN_MAP['W'])
        assertEquals(5, NumerologyCalculator.CHALDEAN_MAP['X'])
        assertEquals(1, NumerologyCalculator.CHALDEAN_MAP['Y'])
        assertEquals(7, NumerologyCalculator.CHALDEAN_MAP['Z'])
    }

    @Test
    fun testPersonalYearAndPersonalDay() {
        // Birth Day: 29 (-> 2), Birth Month: 8 (-> 8), Year: 2026 (2+0+2+6 = 10 -> 1)
        // 2 + 8 + 1 = 11 -> 2
        val personalYear = NumerologyCalculator.calculatePersonalYear(29, 8, 2026)
        assertEquals(2, personalYear)

        // Today: Nov 10 (Month: 11 -> 2, Day: 10 -> 1)
        // Personal Day = 2 (pYear) + 2 (month) + 1 (day) = 5
        val today = LocalDate.of(2026, 11, 10)
        val personalDay = NumerologyCalculator.calculatePersonalDay(personalYear, today)
        assertEquals(5, personalDay)
    }

    @Test
    fun testNumerologyDataInfoAndTips() {
        for (i in 1..9) {
            val info = NumerologyData.getInfo(i)
            assertEquals(i, info.number)
            assertTrue(info.planetNameEn.isNotBlank())
            assertTrue(info.planetNameTe.isNotBlank())
            assertTrue(info.natureEn.isNotBlank())
            assertTrue(info.natureTe.isNotBlank())
            assertTrue(info.luckyDaysEn.isNotEmpty())
            assertTrue(info.luckyColorsEn.isNotEmpty())

            val tip = NumerologyData.getDailyTip(i)
            assertTrue(tip.first.isNotBlank())
            assertTrue(tip.second.isNotBlank())
        }
    }
}
