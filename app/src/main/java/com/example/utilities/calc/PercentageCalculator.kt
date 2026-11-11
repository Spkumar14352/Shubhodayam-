package com.example.utilities.calc

data class DiscountResult(val finalPrice: Double, val saved: Double, val effectiveDiscountPercent: Double)

object PercentageCalculator {
    /** [percent]% of [value]. */
    fun percentOf(percent: Double, value: Double) = value * percent / 100.0

    /** What percent [part] is of [whole]. */
    fun whatPercent(part: Double, whole: Double): Double {
        require(whole != 0.0)
        return part / whole * 100.0
    }

    /** Percent increase (positive) or decrease (negative) from [from] to [to]. */
    fun percentChange(from: Double, to: Double): Double {
        require(from != 0.0)
        return (to - from) / from * 100.0
    }

    /** A discount, optionally followed by a second one (e.g. "20% + extra 10%"). */
    fun discount(price: Double, discountPercent: Double, extraPercent: Double = 0.0): DiscountResult {
        val final = price * (1 - discountPercent / 100.0) * (1 - extraPercent / 100.0)
        val saved = price - final
        return DiscountResult(final, saved, if (price == 0.0) 0.0 else saved / price * 100.0)
    }
}
