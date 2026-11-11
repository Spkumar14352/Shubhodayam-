package com.example.utilities

import com.example.utilities.calc.BmiCalculator
import com.example.utilities.calc.BmiCategory
import com.example.utilities.calc.ChitFundCalculator
import com.example.utilities.calc.EmiCalculator
import com.example.utilities.calc.GstCalculator
import com.example.utilities.calc.IndianFormat
import com.example.utilities.calc.LoanPrepaymentCalculator
import com.example.utilities.calc.NumberToWords
import com.example.utilities.calc.PercentageCalculator
import com.example.utilities.calc.PrepaymentMode
import com.example.utilities.calc.SavingsSchemeCalculator
import com.example.utilities.calc.UnitCategory
import com.example.utilities.calc.UnitConverter
import com.example.utilities.calc.VaddiCalculator
import com.example.utilities.calc.VaddiRateMode
import com.example.utilities.calc.VaddiType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorsTest {
    private val delta = 0.01

    @Test
    fun indianGrouping() {
        assertEquals("12,34,567.50", IndianFormat.number(1234567.5))
        assertEquals("₹1,00,000", IndianFormat.rupees(100000.0, 0))
        assertEquals("999.00", IndianFormat.number(999.0))
        assertEquals("-1,500.00", IndianFormat.number(-1500.0))
        assertEquals(150000.0, IndianFormat.parse("1,50,000")!!, delta)
    }

    @Test
    fun emiMatchesTheStandardFormula() {
        // ₹10 lakh at 8.5% for 20 years.
        val result = EmiCalculator.calculate(1_000_000.0, 8.5, 240)
        assertEquals(8678.23, result.emi, 0.05)
        assertEquals(240, result.schedule.size)
        assertEquals(0.0, result.schedule.last().balance, delta)
        assertEquals(1_000_000.0, result.schedule.sumOf { it.principal }, 0.5)
        assertEquals(1000.0, EmiCalculator.emi(12000.0, 0.0, 12), delta)
    }

    @Test
    fun gstInclusiveAndExclusive() {
        val inclusive = GstCalculator.calculate(1180.0, 18.0, inclusive = true)
        assertEquals(1000.0, inclusive.baseAmount, delta)
        assertEquals(180.0, inclusive.gstAmount, delta)
        assertEquals(90.0, inclusive.cgst, delta)
        assertEquals(90.0, inclusive.sgst, delta)

        val exclusive = GstCalculator.calculate(1000.0, 12.0, inclusive = false)
        assertEquals(1120.0, exclusive.totalAmount, delta)
        assertEquals(120.0, exclusive.igst, delta)
    }

    @Test
    fun bmiAndHealthyRange() {
        val result = BmiCalculator.calculate(70.0, 175.0)
        assertEquals(22.86, result.bmi, delta)
        assertEquals(BmiCategory.NORMAL, result.category)
        assertEquals(56.66, result.healthyMinKg, delta)
        assertEquals(0.0, result.changeToHealthyKg, delta)
        assertEquals(BmiCategory.OBESE, BmiCalculator.calculate(100.0, 170.0).category)
        assertEquals(170.18, BmiCalculator.heightCm(5.0, 7.0), delta)
    }

    @Test
    fun percentagesAndDiscounts() {
        assertEquals(100.0, PercentageCalculator.percentOf(20.0, 500.0), delta)
        assertEquals(25.0, PercentageCalculator.whatPercent(50.0, 200.0), delta)
        assertEquals(-20.0, PercentageCalculator.percentChange(500.0, 400.0), delta)
        val discount = PercentageCalculator.discount(1000.0, 20.0, 10.0)
        assertEquals(720.0, discount.finalPrice, delta)
        assertEquals(28.0, discount.effectiveDiscountPercent, delta)
    }

    @Test
    fun vaddiTwoRupeesIsTwentyFourPercent() {
        val simple = VaddiCalculator.calculate(10000.0, 2.0, VaddiRateMode.RUPEES_PER_100_PER_MONTH, 12, VaddiType.SIMPLE)
        assertEquals(24.0, simple.annualPercent, delta)
        assertEquals(2400.0, simple.totalInterest, delta)
        assertEquals(12, simple.rows.size)

        val monthly = VaddiCalculator.calculate(10000.0, 2.0, VaddiRateMode.RUPEES_PER_100_PER_MONTH, 12, VaddiType.COMPOUND_MONTHLY)
        assertEquals(12682.42, monthly.totalPayable, delta)

        // Yearly compounding: first year simple, second year on ₹12,400.
        val yearly = VaddiCalculator.calculate(10000.0, 24.0, VaddiRateMode.PERCENT_PER_YEAR, 24, VaddiType.COMPOUND_YEARLY)
        assertEquals(15376.0, yearly.totalPayable, delta)
    }

    @Test
    fun chitFund() {
        val result = ChitFundCalculator.calculate(100000.0, 20, 20, 5.0, 20000.0, 5)
        assertEquals(5000.0, result.commissionPerMonth, delta)
        assertEquals(750.0, result.dividendPerMonth, delta)
        assertEquals(4250.0, result.monthlyContribution, delta)
        assertEquals(80000.0, result.amountReceived, delta)
        assertEquals(85000.0, result.totalPaid, delta)
        assertEquals(-5000.0, result.profit, delta)
        assertNotNull(result.annualRatePercent)
    }

    @Test
    fun ppfMaturity() {
        // ₹1.5 lakh a year for 15 years at 7.1% is the widely quoted ₹40,68,209.
        val result = SavingsSchemeCalculator.ppf(150000.0, 7.1)
        assertEquals(4068209.0, result.maturity, 1.0)
        assertEquals(2250000.0, result.totalDeposit, delta)
        assertEquals(15, result.years.size)
    }

    @Test
    fun sukanyaDepositsFifteenYearsGrowsTwentyOne() {
        val result = SavingsSchemeCalculator.sukanya(150000.0, 8.2)
        assertEquals(21, result.years.size)
        assertEquals(0.0, result.years[15].deposit, delta)
        assertEquals(2250000.0, result.totalDeposit, delta)
        assertTrue(result.maturity > result.years[14].balance)
    }

    @Test
    fun epfContributionSplit() {
        // ₹15,000 basic: employee ₹1,800 + employer ₹550 to EPF (₹1,250 goes to EPS).
        assertEquals(2350.0, SavingsSchemeCalculator.epfMonthlyContribution(15000.0), delta)
        val result = SavingsSchemeCalculator.epf(15000.0, 30, 31, 8.25, 0.0)
        // One year of ₹2,350 a month with interest on the monthly running balance.
        assertEquals(28200.0 + 2350.0 * 78 * 8.25 / 1200, result.maturity, delta)
    }

    @Test
    fun prepaymentSavesInterest() {
        val none = LoanPrepaymentCalculator.calculate(1_000_000.0, 8.5, 240, 0.0, PrepaymentMode.ONE_TIME, 12)
        assertEquals(0, none.monthsSaved)
        assertEquals(0.0, none.interestSaved, 1.0)

        val yearly = LoanPrepaymentCalculator.calculate(1_000_000.0, 8.5, 240, 50000.0, PrepaymentMode.YEARLY, 12)
        assertTrue(yearly.monthsSaved > 50)
        assertTrue(yearly.interestSaved > 300000)
    }

    @Test
    fun numbersInEnglish() {
        assertEquals("One Crore Twenty Three Lakh Forty Five Thousand Six Hundred Seventy Eight", NumberToWords.english(12345678))
        assertEquals("One Hundred Crore", NumberToWords.english(1_000_000_000))
        assertEquals("Rupees One Thousand Two Hundred Fifty and Fifty Paise Only", NumberToWords.rupeesEnglish(1250.50))
        assertEquals("Rupees Zero Only", NumberToWords.rupeesEnglish(0.0))
    }

    @Test
    fun numbersInTelugu() {
        assertEquals("నూట ఒకటి", NumberToWords.telugu(101))
        assertEquals("వంద", NumberToWords.telugu(100))
        assertEquals("వెయ్యి ఐదు వందలు", NumberToWords.telugu(1500))
        assertEquals("రెండు లక్షల యాభై వేలు", NumberToWords.telugu(250000))
        assertEquals("లక్ష", NumberToWords.telugu(100000))
        assertEquals("రెండు కోట్లు", NumberToWords.telugu(20_000_000))
        assertEquals("ఇరవై ఒకటి రూపాయలు మాత్రమే", NumberToWords.rupeesTelugu(21.0))
    }

    @Test
    fun unitConversions() {
        assertEquals(11.6638, UnitConverter.convert(1.0, UnitCategory.WEIGHT, "tola", "g"), 1e-6)
        assertEquals(100.0, UnitConverter.convert(1.0, UnitCategory.WEIGHT, "quintal", "kg"), 1e-9)
        assertEquals(0.3048, UnitConverter.convert(1.0, UnitCategory.LENGTH, "ft", "m"), 1e-9)
        assertEquals(212.0, UnitConverter.convert(100.0, UnitCategory.TEMPERATURE, "c", "f"), 1e-9)
        assertEquals(43560.0, UnitConverter.convert(1.0, UnitCategory.AREA, "acre", "sqft"), 1e-6)
        assertEquals(100.0, UnitConverter.convert(1.0, UnitCategory.AREA, "acre", "cent"), 1e-6)
        assertEquals(5.5, UnitConverter.feetInches(5.0, 6.0), 1e-9)
        val (feet, inches) = UnitConverter.toFeetInches(5.5)
        assertEquals(5, feet)
        assertEquals(6.0, inches, 1e-9)
    }
}
