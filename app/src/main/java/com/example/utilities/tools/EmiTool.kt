package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.EmiCalculator
import com.example.utilities.calc.EmiResult
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.ToolTable
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t
import kotlin.math.roundToInt

class EmiViewModel : ToolViewModel<EmiViewModel.Input>(Input()) {
    data class Input(val amount: String = "", val rate: String = "", val years: String = "", val showSchedule: Boolean = false)

    fun result(input: Input): EmiResult? {
        val amount = num(input.amount) ?: return null
        val rate = num(input.rate) ?: return null
        val months = ((num(input.years) ?: return null) * 12).roundToInt()
        if (amount <= 0 || months !in 1..600 || rate > 100) return null
        return EmiCalculator.calculate(amount, rate, months)
    }
}

/** EMI Calculator: replaces the separate EMI and EMI Planner tools (basic + schedule tab). */
@Composable
fun EmiScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: EmiViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        note = ToolNote.FINANCE,
        shareText = result?.let {
            language.t(
                "EMI: ${rupees(it.emi)} / నెల\nమొత్తం వడ్డీ: ${rupees(it.totalInterest)}\nమొత్తం చెల్లింపు: ${rupees(it.totalPayment)}",
                "EMI: ${rupees(it.emi)} / month\nTotal interest: ${rupees(it.totalInterest)}\nTotal payment: ${rupees(it.totalPayment)}"
            )
        },
        inputs = {
            ToolChoices(listOf(false to language.t("సారాంశం", "Summary"), true to language.t("చెల్లింపు పట్టిక", "Schedule")), input.showSchedule) { v -> vm.update { it.copy(showSchedule = v) } }
            ToolNumberField(language.t("రుణ మొత్తం", "Loan amount"), input.amount, { v -> vm.update { it.copy(amount = v) } }, suffix = "₹")
            ToolNumberField(language.t("వడ్డీ రేటు (సంవత్సరానికి)", "Interest rate (per year)"), input.rate, { v -> vm.update { it.copy(rate = v) } }, suffix = "%")
            ToolNumberField(language.t("కాలవ్యవధి (సంవత్సరాలు)", "Tenure (years)"), input.years, { v -> vm.update { it.copy(years = v) } })
        },
        result = result?.let {
            {
                ResultHeadline(language.t("నెలవారీ EMI", "Monthly EMI"), rupees(it.emi))
                ResultRow(language.t("మొత్తం వడ్డీ", "Total interest"), rupees(it.totalInterest))
                ResultRow(language.t("మొత్తం చెల్లింపు", "Total payment"), rupees(it.totalPayment))
                ResultRow(language.t("నెలలు", "Months"), it.schedule.size.toString())
            }
        },
        extra = result?.takeIf { input.showSchedule }?.let {
            {
                ToolTable(
                    title = language.t("నెలవారీ చెల్లింపు పట్టిక", "Month-by-month schedule"),
                    headers = listOf(language.t("నెల", "Month"), "EMI", language.t("అసలు", "Principal"), language.t("వడ్డీ", "Interest"), language.t("బాకీ", "Balance")),
                    rows = it.schedule.map { row -> listOf(row.month.toString(), rupees(row.emi), rupees(row.principal), rupees(row.interest), rupees(row.balance)) }
                )
            }
        }
    )
}
