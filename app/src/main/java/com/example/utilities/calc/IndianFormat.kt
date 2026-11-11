package com.example.utilities.calc

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

/** Indian digit grouping: 12,34,567.89. */
object IndianFormat {
    fun number(value: Double, decimals: Int = 2): String {
        val rounded = BigDecimal(abs(value)).setScale(decimals, RoundingMode.HALF_UP)
        val plain = rounded.toPlainString()
        val whole = plain.substringBefore('.')
        val fraction = plain.substringAfter('.', "")
        val grouped = if (whole.length <= 3) {
            whole
        } else {
            val head = whole.dropLast(3)
            head.reversed().chunked(2).joinToString(",").reversed() + "," + whole.takeLast(3)
        }
        val sign = if (value < 0 && rounded.signum() != 0) "-" else ""
        return sign + grouped + if (fraction.isNotEmpty()) ".$fraction" else ""
    }

    fun rupees(value: Double, decimals: Int = 2): String = "₹" + number(value, decimals)

    /** Parses user input such as "1,50,000" or " 2.5 "; null when it isn't a number. */
    fun parse(text: String): Double? = text.replace(",", "").replace("₹", "").trim().toDoubleOrNull()
}
