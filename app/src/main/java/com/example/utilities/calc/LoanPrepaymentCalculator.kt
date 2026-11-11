package com.example.utilities.calc

enum class PrepaymentMode { ONE_TIME, YEARLY }

data class PrepaymentResult(
    val emi: Double,
    val originalMonths: Int,
    val newMonths: Int,
    val originalInterest: Double,
    val newInterest: Double
) {
    val interestSaved: Double get() = originalInterest - newInterest
    val monthsSaved: Int get() = originalMonths - newMonths
}

/** Prepayment keeps the EMI the same and shortens the loan, the usual bank default. */
object LoanPrepaymentCalculator {
    fun calculate(
        principal: Double,
        annualRatePercent: Double,
        months: Int,
        prepayment: Double,
        mode: PrepaymentMode,
        startMonth: Int
    ): PrepaymentResult {
        val emi = EmiCalculator.emi(principal, annualRatePercent, months)
        val originalInterest = emi * months - principal
        val r = annualRatePercent / 12.0 / 100.0

        var balance = principal
        var interestPaid = 0.0
        var month = 0
        while (balance > 0.005 && month < months) {
            month++
            val interest = balance * r
            interestPaid += interest
            balance -= minOf(emi - interest, balance)
            val isPrepaymentMonth = when (mode) {
                PrepaymentMode.ONE_TIME -> month == startMonth
                PrepaymentMode.YEARLY -> month >= startMonth && (month - startMonth) % 12 == 0
            }
            if (isPrepaymentMonth) balance -= minOf(prepayment, balance)
        }
        return PrepaymentResult(emi, months, month, originalInterest, interestPaid)
    }
}
