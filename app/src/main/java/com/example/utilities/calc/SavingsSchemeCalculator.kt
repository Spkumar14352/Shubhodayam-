package com.example.utilities.calc

data class SchemeYear(val year: Int, val deposit: Double, val interest: Double, val balance: Double)

data class SchemeResult(val totalDeposit: Double, val totalInterest: Double, val maturity: Double, val years: List<SchemeYear>)

/** PPF, Sukanya Samriddhi and EPF maturity, compounded yearly as these schemes credit interest. */
object SavingsSchemeCalculator {
    const val PPF_YEARS = 15
    const val SSY_DEPOSIT_YEARS = 15
    const val SSY_MATURITY_YEARS = 21
    const val EPF_EMPLOYEE_PERCENT = 12.0
    const val EPS_PERCENT = 8.33
    const val EPS_WAGE_CEILING = 15000.0

    /** Yearly deposit made at the start of each year (before the 5th of April for PPF). */
    fun ppf(yearlyDeposit: Double, annualRatePercent: Double, years: Int = PPF_YEARS): SchemeResult =
        yearly(yearlyDeposit, annualRatePercent, depositYears = years, totalYears = years)

    /** Deposits for 15 years; the account keeps earning interest until 21 years from opening. */
    fun sukanya(yearlyDeposit: Double, annualRatePercent: Double): SchemeResult =
        yearly(yearlyDeposit, annualRatePercent, SSY_DEPOSIT_YEARS, SSY_MATURITY_YEARS)

    private fun yearly(deposit: Double, ratePercent: Double, depositYears: Int, totalYears: Int): SchemeResult {
        var balance = 0.0
        val rows = (1..totalYears).map { year ->
            val paid = if (year <= depositYears) deposit else 0.0
            val interest = (balance + paid) * ratePercent / 100.0
            balance += paid + interest
            SchemeYear(year, paid, interest, balance)
        }
        val deposited = rows.sumOf { it.deposit }
        return SchemeResult(deposited, balance - deposited, balance, rows)
    }

    /** Monthly employee + employer EPF share. The employer's 8.33% EPS part (on up to ₹15,000) goes to pension instead. */
    fun epfMonthlyContribution(basicPlusDa: Double): Double {
        val employee = basicPlusDa * EPF_EMPLOYEE_PERCENT / 100.0
        val employerToEps = minOf(basicPlusDa, EPS_WAGE_CEILING) * EPS_PERCENT / 100.0
        val employerToEpf = basicPlusDa * EPF_EMPLOYEE_PERCENT / 100.0 - employerToEps
        return employee + employerToEpf
    }

    /**
     * EPF until retirement. Interest is worked out on each month's running balance and credited at
     * the end of the year, as EPFO does. Salary rises by [yearlyIncreasePercent] each year.
     */
    fun epf(
        basicPlusDa: Double,
        currentAge: Int,
        retirementAge: Int,
        annualRatePercent: Double,
        yearlyIncreasePercent: Double,
        openingBalance: Double = 0.0
    ): SchemeResult {
        require(retirementAge > currentAge)
        var balance = openingBalance
        var salary = basicPlusDa
        val rows = (1..(retirementAge - currentAge)).map { year ->
            val monthly = epfMonthlyContribution(salary)
            var monthlyBalancesSum = 0.0
            repeat(12) {
                balance += monthly
                monthlyBalancesSum += balance
            }
            val interest = monthlyBalancesSum * annualRatePercent / 100.0 / 12.0
            balance += interest
            salary *= 1 + yearlyIncreasePercent / 100.0
            SchemeYear(year, monthly * 12, interest, balance)
        }
        val deposited = rows.sumOf { it.deposit }
        return SchemeResult(deposited, balance - deposited - openingBalance, balance, rows)
    }
}
