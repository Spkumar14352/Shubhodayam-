package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.data.utilities.InterestRates
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.SavingsSchemeCalculator
import com.example.utilities.calc.SchemeResult
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.ToolTable
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t

class PpfViewModel : ToolViewModel<PpfViewModel.Input>(Input()) {
    data class Input(val yearly: String = "")

    fun result(input: Input): SchemeResult? {
        val yearly = num(input.yearly) ?: return null
        if (yearly !in 500.0..150000.0) return null
        return SavingsSchemeCalculator.ppf(yearly, InterestRates.PPF_ANNUAL_RATE)
    }
}

class SsyViewModel : ToolViewModel<SsyViewModel.Input>(Input()) {
    data class Input(val yearly: String = "")

    fun result(input: Input): SchemeResult? {
        val yearly = num(input.yearly) ?: return null
        if (yearly !in 250.0..150000.0) return null
        return SavingsSchemeCalculator.sukanya(yearly, InterestRates.SSY_ANNUAL_RATE)
    }
}

class EpfViewModel : ToolViewModel<EpfViewModel.Input>(Input()) {
    data class Input(val basic: String = "", val age: String = "", val retire: String = "58", val raise: String = "5", val balance: String = "")

    fun result(input: Input): SchemeResult? {
        val basic = num(input.basic) ?: return null
        val age = num(input.age)?.toInt() ?: return null
        val retire = num(input.retire)?.toInt() ?: return null
        val raise = num(input.raise) ?: 0.0
        if (basic <= 0 || age !in 15..70 || retire !in (age + 1)..75 || raise !in 0.0..50.0) return null
        return SavingsSchemeCalculator.epf(basic, age, retire, InterestRates.EPF_ANNUAL_RATE, raise, num(input.balance) ?: 0.0)
    }
}

@Composable
private fun SchemeScreen(
    language: Language,
    title: String,
    rate: Double,
    result: SchemeResult?,
    onBack: () -> Unit,
    onReset: () -> Unit,
    inputs: @Composable () -> Unit
) {
    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = onReset,
        note = ToolNote.FINANCE,
        shareText = result?.let {
            "$title\n${language.t("మెచ్యూరిటీ", "Maturity")}: ${rupees(it.maturity)}\n${language.t("మొత్తం జమ", "Total deposited")}: ${rupees(it.totalDeposit)}\n" +
                "${language.t("వడ్డీ", "Interest")}: ${rupees(it.totalInterest)}\n${language.t("వాడిన రేటు", "Rate used")}: $rate%"
        },
        inputs = { inputs() },
        result = result?.let {
            {
                ResultHeadline(language.t("మెచ్యూరిటీ మొత్తం", "Maturity amount"), rupees(it.maturity))
                ResultRow(language.t("మొత్తం జమ", "Total deposited"), rupees(it.totalDeposit))
                ResultRow(language.t("మొత్తం వడ్డీ", "Total interest"), rupees(it.totalInterest))
                ResultRow(language.t("వాడిన రేటు", "Rate used"), "$rate%")
            }
        },
        extra = result?.let {
            {
                ToolTable(
                    title = language.t("సంవత్సరం వారీ పట్టిక", "Year-wise table"),
                    headers = listOf(language.t("సంవత్సరం", "Year"), language.t("జమ", "Deposit"), language.t("వడ్డీ", "Interest"), language.t("నిల్వ", "Balance")),
                    rows = it.years.map { y -> listOf(y.year.toString(), rupees(y.deposit), rupees(y.interest), rupees(y.balance)) }
                )
            }
        }
    )
}

@Composable
fun PpfScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: PpfViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    SchemeScreen(language, title, InterestRates.PPF_ANNUAL_RATE, result, onBack, vm::reset) {
        ToolNumberField(language.t("సంవత్సర జమ (₹500 – ₹1,50,000)", "Yearly deposit (₹500 – ₹1,50,000)"), input.yearly, { v -> vm.update { it.copy(yearly = v) } }, suffix = "₹")
    }
}

@Composable
fun SsyScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: SsyViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    SchemeScreen(language, title, InterestRates.SSY_ANNUAL_RATE, result, onBack, vm::reset) {
        ToolNumberField(language.t("సంవత్సర జమ (₹250 – ₹1,50,000)", "Yearly deposit (₹250 – ₹1,50,000)"), input.yearly, { v -> vm.update { it.copy(yearly = v) } }, suffix = "₹")
    }
}

@Composable
fun EpfScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: EpfViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    SchemeScreen(language, title, InterestRates.EPF_ANNUAL_RATE, result, onBack, vm::reset) {
        ToolNumberField(language.t("నెల బేసిక్ + DA", "Monthly basic + DA"), input.basic, { v -> vm.update { it.copy(basic = v) } }, suffix = "₹")
        ToolNumberField(language.t("ప్రస్తుత వయస్సు", "Current age"), input.age, { v -> vm.update { it.copy(age = v) } }, decimal = false)
        ToolNumberField(language.t("పదవీ విరమణ వయస్సు", "Retirement age"), input.retire, { v -> vm.update { it.copy(retire = v) } }, decimal = false)
        ToolNumberField(language.t("సంవత్సర జీతం పెంపు", "Yearly salary increase"), input.raise, { v -> vm.update { it.copy(raise = v) } }, suffix = "%")
        ToolNumberField(language.t("ప్రస్తుత PF నిల్వ (ఐచ్ఛికం)", "Current PF balance (optional)"), input.balance, { v -> vm.update { it.copy(balance = v) } }, suffix = "₹")
    }
}
