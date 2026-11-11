package com.example.utilities.calc

import kotlin.math.pow

data class EmiRow(val month: Int, val emi: Double, val interest: Double, val principal: Double, val balance: Double)

data class EmiResult(val emi: Double, val totalInterest: Double, val totalPayment: Double, val schedule: List<EmiRow>)

/** Reducing-balance EMI with a month-by-month amortisation schedule. */
object EmiCalculator {
    fun emi(principal: Double, annualRatePercent: Double, months: Int): Double {
        require(principal >= 0 && months > 0)
        val r = annualRatePercent / 12.0 / 100.0
        if (r == 0.0) return principal / months
        val growth = (1 + r).pow(months)
        return principal * r * growth / (growth - 1)
    }

    fun calculate(principal: Double, annualRatePercent: Double, months: Int): EmiResult {
        val emi = emi(principal, annualRatePercent, months)
        val r = annualRatePercent / 12.0 / 100.0
        var balance = principal
        val rows = (1..months).map { month ->
            val interest = balance * r
            // The last instalment clears any rounding remainder.
            val principalPart = if (month == months) balance else emi - interest
            balance = (balance - principalPart).coerceAtLeast(0.0)
            EmiRow(month, interest + principalPart, interest, principalPart, balance)
        }
        val total = rows.sumOf { it.emi }
        return EmiResult(emi, total - principal, total, rows)
    }
}
