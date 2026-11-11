package com.example.utilities.calc

import java.math.BigDecimal
import java.math.RoundingMode

/** Indian numbering (thousand, lakh, crore) in English and Telugu, including cheque wording. */
object NumberToWords {
    private val ONES = listOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )
    private val TENS = listOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

    private val TE_ONES = listOf(
        "", "ఒకటి", "రెండు", "మూడు", "నాలుగు", "ఐదు", "ఆరు", "ఏడు", "ఎనిమిది", "తొమ్మిది", "పది",
        "పదకొండు", "పన్నెండు", "పదమూడు", "పద్నాలుగు", "పదిహేను", "పదహారు", "పదిహేడు", "పద్దెనిమిది", "పందొమ్మిది"
    )
    private val TE_TENS = listOf("", "", "ఇరవై", "ముప్పై", "నలభై", "యాభై", "అరవై", "డెబ్బై", "ఎనభై", "తొంభై")

    // ---------- English ----------

    fun english(number: Long): String {
        require(number >= 0)
        if (number == 0L) return "Zero"
        return englishParts(number).joinToString(" ")
    }

    private fun englishParts(n: Long): List<String> {
        val parts = mutableListOf<String>()
        val crore = n / 10_000_000
        val lakh = n / 100_000 % 100
        val thousand = n / 1000 % 100
        val hundred = n / 100 % 10
        val rest = (n % 100).toInt()
        if (crore > 0) parts += englishParts(crore) + "Crore"
        if (lakh > 0) parts += englishBelow100(lakh.toInt()) + " Lakh"
        if (thousand > 0) parts += englishBelow100(thousand.toInt()) + " Thousand"
        if (hundred > 0) parts += ONES[hundred.toInt()] + " Hundred"
        if (rest > 0) parts += englishBelow100(rest)
        return parts
    }

    private fun englishBelow100(n: Int) = if (n < 20) ONES[n] else listOf(TENS[n / 10], ONES[n % 10]).filter { it.isNotEmpty() }.joinToString(" ")

    /** "Rupees One Thousand Two Hundred Fifty and Fifty Paise Only". */
    fun rupeesEnglish(amount: Double): String {
        val (rupees, paise) = split(amount)
        val paisePart = if (paise > 0) " and ${english(paise.toLong())} Paise" else ""
        return "Rupees ${english(rupees)}$paisePart Only"
    }

    // ---------- Telugu ----------

    fun telugu(number: Long): String {
        require(number >= 0)
        if (number == 0L) return "సున్నా"
        return teluguWords(number)
    }

    // Each place has a stand-alone form ("వంద", "వేలు") and a joining form used when more follows ("నూట", "వేల").
    private fun teluguWords(n: Long): String {
        val crore = n / 10_000_000
        val lakh = (n / 100_000 % 100).toInt()
        val thousand = (n / 1000 % 100).toInt()
        val hundred = (n / 100 % 10).toInt()
        val rest = (n % 100).toInt()
        val parts = mutableListOf<String>()

        fun more(vararg later: Int) = later.any { it > 0 }

        if (crore > 0) {
            val last = !more(lakh, thousand, hundred, rest)
            parts += if (crore == 1L) "కోటి" else "${teluguWords(crore)} ${if (last) "కోట్లు" else "కోట్ల"}"
        }
        if (lakh > 0) {
            val last = !more(thousand, hundred, rest)
            parts += if (lakh == 1) (if (last) "లక్ష" else "లక్షా") else "${teluguBelow100(lakh)} ${if (last) "లక్షలు" else "లక్షల"}"
        }
        if (thousand > 0) {
            val last = !more(hundred, rest)
            parts += if (thousand == 1) "వెయ్యి" else "${teluguBelow100(thousand)} ${if (last) "వేలు" else "వేల"}"
        }
        if (hundred > 0) {
            val last = rest == 0
            parts += when {
                hundred == 1 -> if (last) "వంద" else "నూట"
                else -> "${TE_ONES[hundred]} ${if (last) "వందలు" else "వందల"}"
            }
        }
        if (rest > 0) parts += teluguBelow100(rest)
        return parts.joinToString(" ")
    }

    private fun teluguBelow100(n: Int) = if (n < 20) TE_ONES[n] else listOf(TE_TENS[n / 10], TE_ONES[n % 10]).filter { it.isNotEmpty() }.joinToString(" ")

    /** "రెండు వేల ఐదు వందలు రూపాయలు యాభై పైసలు మాత్రమే". */
    fun rupeesTelugu(amount: Double): String {
        val (rupees, paise) = split(amount)
        val paisePart = if (paise > 0) " ${telugu(paise.toLong())} పైసలు" else ""
        return "${telugu(rupees)} రూపాయలు$paisePart మాత్రమే"
    }

    private fun split(amount: Double): Pair<Long, Int> {
        require(amount >= 0)
        val rounded = BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)
        val rupees = rounded.toBigInteger().toLong()
        val paise = rounded.subtract(BigDecimal(rupees)).movePointRight(2).toInt()
        return rupees to paise
    }
}
