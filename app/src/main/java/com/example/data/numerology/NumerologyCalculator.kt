package com.example.data.numerology

import java.time.LocalDate

/**
 * Pure Kotlin calculator for Vedic & Chaldean Numerology calculations.
 */
object NumerologyCalculator {

    /**
     * Moolank (Birth/Psychic number) = digit sum of the birth DAY reduced to 1–9.
     * E.g. day 29 -> 2 + 9 = 11 -> 1 + 1 = 2.
     */
    fun calculateMoolank(dayOfMonth: Int): Int {
        require(dayOfMonth in 1..31) { "Day of month must be between 1 and 31" }
        return reduceToSingleDigit(dayOfMonth)
    }

    /**
     * Bhagyank (Destiny/Life path number) = digit sum of full date DD + MM + YYYY reduced to 1–9.
     * Keeps track of master numbers 11 and 22.
     */
    data class BhagyankResult(
        val singleDigit: Int,
        val masterNumber: Int? = null,
        val compoundTotal: Int
    )

    fun calculateBhagyank(date: LocalDate): BhagyankResult {
        // Sum all digits in day, month, and year
        val dateString = "%02d%02d%04d".format(date.dayOfMonth, date.monthValue, date.year)
        val initialSum = dateString.map { it.digitToInt() }.sum()

        // Check for master numbers before reducing to single digit if intermediate is 11 or 22
        var current = initialSum
        var master: Int? = null

        if (current == 11 || current == 22) {
            master = current
        }

        while (current > 9) {
            val nextSum = current.toString().map { it.digitToInt() }.sum()
            if (master == null && (nextSum == 11 || nextSum == 22)) {
                master = nextSum
            }
            current = nextSum
        }

        return BhagyankResult(
            singleDigit = current,
            masterNumber = master,
            compoundTotal = initialSum
        )
    }

    /**
     * Chaldean System Values:
     * A=1, B=2, C=3, D=4, E=5, F=8, G=3, H=5, I=1, J=1, K=2, L=3, M=4, N=5,
     * O=7, P=8, Q=1, R=2, S=3, T=4, U=6, V=6, W=6, X=5, Y=1, Z=7.
     *
     * Ignores spaces, dots, digits and punctuation.
     */
    val CHALDEAN_MAP: Map<Char, Int> = mapOf(
        'A' to 1, 'I' to 1, 'J' to 1, 'Q' to 1, 'Y' to 1,
        'B' to 2, 'K' to 2, 'R' to 2,
        'C' to 3, 'G' to 3, 'L' to 3, 'S' to 3,
        'D' to 4, 'M' to 4, 'T' to 4,
        'E' to 5, 'H' to 5, 'N' to 5, 'X' to 5,
        'U' to 6, 'V' to 6, 'W' to 6,
        'O' to 7, 'Z' to 7,
        'F' to 8, 'P' to 8
    )

    data class NameNumberResult(
        val compoundTotal: Int,
        val singleDigit: Int,
        val breakdown: List<Pair<Char, Int>>
    )

    fun calculateNameNumber(name: String): NameNumberResult {
        val uppercaseName = name.uppercase()
        val breakdown = mutableListOf<Pair<Char, Int>>()
        var total = 0

        for (ch in uppercaseName) {
            val value = CHALDEAN_MAP[ch]
            if (value != null) {
                breakdown.add(ch to value)
                total += value
            }
        }

        val singleDigit = if (total == 0) 0 else reduceToSingleDigit(total)
        return NameNumberResult(
            compoundTotal = total,
            singleDigit = singleDigit,
            breakdown = breakdown
        )
    }

    /**
     * Personal Year = sum of birth day + birth month + current year, reduced to 1-9.
     */
    fun calculatePersonalYear(birthDay: Int, birthMonth: Int, currentYear: Int): Int {
        val sum = reduceToSingleDigit(birthDay) + reduceToSingleDigit(birthMonth) + reduceToSingleDigit(currentYear)
        return reduceToSingleDigit(sum)
    }

    /**
     * Personal Day = personal year + current month + current day, reduced to 1-9.
     */
    fun calculatePersonalDay(personalYear: Int, today: LocalDate): Int {
        val sum = personalYear + reduceToSingleDigit(today.monthValue) + reduceToSingleDigit(today.dayOfMonth)
        return reduceToSingleDigit(sum)
    }

    /**
     * Reduces any positive integer into a single digit 1..9 by repeatedly summing digits.
     */
    fun reduceToSingleDigit(num: Int): Int {
        var n = num
        while (n > 9) {
            n = n.toString().map { it.digitToInt() }.sum()
        }
        return n
    }

    /**
     * Suggest subtle alternate spelling variations (only duplicate or common alternate vowels/letters,
     * e.g., 'I'/'EE', 'A'/'AA', 'U'/'OO', 'V'/'W', 'S'/'SH') that yield a target or friendly number.
     * Never changes the base name completely.
     */
    fun suggestNameVariations(name: String, targetFriendlyNumbers: Set<Int>): List<Pair<String, NameNumberResult>> {
        val cleanName = name.trim().filter { it.isLetter() }
        if (cleanName.length < 2) return emptyList()

        val candidates = mutableSetOf<String>()
        val upper = cleanName.uppercase()

        // 1. Double common vowels: A -> AA, E -> EE, I -> II
        for (i in upper.indices) {
            val c = upper[i]
            if (c in listOf('A', 'E', 'I', 'O', 'U')) {
                candidates.add(upper.substring(0, i) + c + upper.substring(i))
            }
        }

        // 2. Common transliteration substitutions
        if (upper.contains("EE")) candidates.add(upper.replaceFirst("EE", "I"))
        if (upper.contains("I")) candidates.add(upper.replaceFirst("I", "EE"))
        if (upper.contains("OO")) candidates.add(upper.replaceFirst("OO", "U"))
        if (upper.contains("U")) candidates.add(upper.replaceFirst("U", "OO"))
        if (upper.contains("V")) candidates.add(upper.replaceFirst("V", "W"))
        if (upper.contains("W")) candidates.add(upper.replaceFirst("W", "V"))
        if (upper.contains("K")) candidates.add(upper.replaceFirst("K", "C"))
        if (upper.contains("C") && !upper.contains("CH")) candidates.add(upper.replaceFirst("C", "K"))

        // Add trailing 'A' or 'H' if common
        candidates.add(upper + "H")
        candidates.add(upper + "A")

        candidates.remove(upper)

        val results = candidates.map { candidate ->
            candidate to calculateNameNumber(candidate)
        }.filter { it.second.singleDigit in targetFriendlyNumbers }
        .distinctBy { it.first }
        .take(5)

        return results
    }
}
