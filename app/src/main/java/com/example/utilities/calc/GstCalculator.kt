package com.example.utilities.calc

data class GstResult(
    val baseAmount: Double,
    val gstAmount: Double,
    val totalAmount: Double,
    /** Within one state: half CGST, half SGST. */
    val cgst: Double,
    val sgst: Double,
    /** Between states: all IGST. */
    val igst: Double
)

object GstCalculator {
    /** [inclusive] means [amount] already contains GST. */
    fun calculate(amount: Double, ratePercent: Double, inclusive: Boolean): GstResult {
        val base = if (inclusive) amount * 100.0 / (100.0 + ratePercent) else amount
        val gst = base * ratePercent / 100.0
        return GstResult(base, gst, base + gst, gst / 2, gst / 2, gst)
    }

    val COMMON_RATES = listOf(0.0, 5.0, 12.0, 18.0, 28.0)
}
