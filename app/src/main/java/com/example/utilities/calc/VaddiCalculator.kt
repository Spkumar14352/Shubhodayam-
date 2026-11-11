package com.example.utilities.calc

enum class VaddiRateMode { RUPEES_PER_100_PER_MONTH, PERCENT_PER_YEAR }

enum class VaddiType { SIMPLE, COMPOUND_MONTHLY, COMPOUND_YEARLY }

data class VaddiRow(val month: Int, val interestThisMonth: Double, val interestSoFar: Double, val amountDue: Double)

data class VaddiResult(val annualPercent: Double, val totalInterest: Double, val totalPayable: Double, val rows: List<VaddiRow>)

/**
 * Village-style interest ("vaddi"). "₹2 vaddi" means ₹2 per ₹100 per month, i.e. 24% a year.
 */
object VaddiCalculator {
    fun annualPercent(rate: Double, mode: VaddiRateMode) =
        if (mode == VaddiRateMode.RUPEES_PER_100_PER_MONTH) rate * 12 else rate

    fun calculate(principal: Double, rate: Double, mode: VaddiRateMode, months: Int, type: VaddiType): VaddiResult {
        require(principal >= 0 && months > 0)
        val annual = annualPercent(rate, mode)
        val monthly = annual / 12.0 / 100.0
        var balance = principal
        // For yearly compounding, interest builds on the year's opening balance and is added each 12 months.
        var yearOpening = principal
        var accruedThisYear = 0.0
        var interestSoFar = 0.0

        val rows = (1..months).map { month ->
            val interest = when (type) {
                VaddiType.SIMPLE -> principal * monthly
                VaddiType.COMPOUND_MONTHLY -> balance * monthly
                VaddiType.COMPOUND_YEARLY -> yearOpening * monthly
            }
            interestSoFar += interest
            when (type) {
                VaddiType.SIMPLE -> Unit
                VaddiType.COMPOUND_MONTHLY -> balance += interest
                VaddiType.COMPOUND_YEARLY -> {
                    accruedThisYear += interest
                    if (month % 12 == 0) {
                        yearOpening += accruedThisYear
                        accruedThisYear = 0.0
                    }
                }
            }
            VaddiRow(month, interest, interestSoFar, principal + interestSoFar)
        }
        return VaddiResult(annual, interestSoFar, principal + interestSoFar, rows)
    }
}
