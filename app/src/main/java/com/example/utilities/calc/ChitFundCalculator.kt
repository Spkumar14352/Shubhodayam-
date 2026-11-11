package com.example.utilities.calc

import kotlin.math.abs
import kotlin.math.pow

data class ChitResult(
    val monthlyContribution: Double,
    val dividendPerMonth: Double,
    val commissionPerMonth: Double,
    val amountReceived: Double,
    val totalPaid: Double,
    val profit: Double,
    /** Approximate yearly rate implied by the cash flows; null if it can't be found. */
    val annualRatePercent: Double?
)

/**
 * A chit where the winner each month gives up a bid (discount) from the chit value. The foreman's
 * commission comes out of that discount and the rest is shared as a dividend among all members.
 *
 * Simplification: every month's auction is assumed to have the same bid as the one entered.
 */
object ChitFundCalculator {
    fun calculate(
        chitValue: Double,
        members: Int,
        months: Int,
        commissionPercent: Double,
        bidAmount: Double,
        winningMonth: Int
    ): ChitResult {
        require(chitValue > 0 && members > 0 && months > 0 && winningMonth in 1..months)
        val commission = chitValue * commissionPercent / 100.0
        val dividend = (bidAmount - commission).coerceAtLeast(0.0) / members
        val contribution = chitValue / members - dividend
        val received = chitValue - bidAmount
        val totalPaid = contribution * months
        return ChitResult(
            monthlyContribution = contribution,
            dividendPerMonth = dividend,
            commissionPerMonth = commission,
            amountReceived = received,
            totalPaid = totalPaid,
            profit = received - totalPaid,
            annualRatePercent = monthlyRate(contribution, received, months, winningMonth)?.let { it * 12 * 100 }
        )
    }

    /**
     * The monthly rate r at which paying [contribution] at the start of months 1..[months] and receiving
     * [received] at month [winningMonth] are worth the same (an internal rate of return).
     */
    private fun monthlyRate(contribution: Double, received: Double, months: Int, winningMonth: Int): Double? {
        fun value(r: Double): Double =
            received / (1 + r).pow(winningMonth - 1) - (0 until months).sumOf { contribution / (1 + r).pow(it) }

        // Value moves one way with r; find a bracket that changes sign, then bisect.
        var low = -0.5
        var high = 0.5
        if (value(low) * value(high) > 0) return null
        repeat(200) {
            val mid = (low + high) / 2
            if (value(low) * value(mid) <= 0) high = mid else low = mid
        }
        val rate = (low + high) / 2
        return if (abs(value(rate)) < 1.0) rate else null
    }
}
