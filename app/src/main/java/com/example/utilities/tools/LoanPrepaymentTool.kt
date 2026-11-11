package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.LoanPrepaymentCalculator
import com.example.utilities.calc.PrepaymentMode
import com.example.utilities.calc.PrepaymentResult
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t
import kotlin.math.roundToInt

class LoanPrepaymentViewModel : ToolViewModel<LoanPrepaymentViewModel.Input>(Input()) {
    data class Input(
        val amount: String = "",
        val rate: String = "",
        val years: String = "",
        val prepayment: String = "",
        val mode: PrepaymentMode = PrepaymentMode.YEARLY,
        val startMonth: String = "12"
    )

    fun result(input: Input): PrepaymentResult? {
        val amount = num(input.amount) ?: return null
        val rate = num(input.rate) ?: return null
        val months = ((num(input.years) ?: return null) * 12).roundToInt()
        val prepayment = num(input.prepayment) ?: return null
        val start = num(input.startMonth)?.toInt() ?: return null
        if (amount <= 0 || months !in 1..600 || rate !in 0.0..100.0 || start !in 1..months || prepayment < 0) return null
        return LoanPrepaymentCalculator.calculate(amount, rate, months, prepayment, input.mode, start)
    }
}

@Composable
fun LoanPrepaymentScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: LoanPrepaymentViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    fun duration(months: Int) = language.t("${months / 12} సం ${months % 12} నె", "${months / 12} y ${months % 12} m")

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        note = ToolNote.FINANCE,
        shareText = result?.let {
            "${language.t("ఆదా అయ్యే వడ్డీ", "Interest saved")}: ${rupees(it.interestSaved)}\n" +
                "${language.t("తగ్గే కాలం", "Tenure reduced by")}: ${duration(it.monthsSaved)}\n" +
                "${language.t("కొత్త కాలం", "New tenure")}: ${duration(it.newMonths)}"
        },
        inputs = {
            ToolNumberField(language.t("రుణ మొత్తం", "Loan amount"), input.amount, { v -> vm.update { it.copy(amount = v) } }, suffix = "₹")
            ToolNumberField(language.t("వడ్డీ రేటు (సంవత్సరానికి)", "Interest rate (per year)"), input.rate, { v -> vm.update { it.copy(rate = v) } }, suffix = "%")
            ToolNumberField(language.t("కాలవ్యవధి (సంవత్సరాలు)", "Tenure (years)"), input.years, { v -> vm.update { it.copy(years = v) } })
            ToolChoices(
                listOf(PrepaymentMode.YEARLY to language.t("ప్రతి సంవత్సరం", "Every year"), PrepaymentMode.ONE_TIME to language.t("ఒక్కసారి", "One time")),
                input.mode
            ) { v -> vm.update { it.copy(mode = v) } }
            ToolNumberField(language.t("ముందస్తు చెల్లింపు", "Prepayment amount"), input.prepayment, { v -> vm.update { it.copy(prepayment = v) } }, suffix = "₹")
            ToolNumberField(language.t("ఏ నెల నుండి", "Starting at month"), input.startMonth, { v -> vm.update { it.copy(startMonth = v) } }, decimal = false)
        },
        result = result?.let {
            {
                ResultHeadline(language.t("ఆదా అయ్యే వడ్డీ", "Interest saved"), rupees(it.interestSaved))
                ResultRow(language.t("తగ్గే కాలం", "Tenure reduced by"), duration(it.monthsSaved))
                ResultRow(language.t("కొత్త కాలం", "New tenure"), duration(it.newMonths))
                ResultRow("EMI", rupees(it.emi))
                ResultRow(language.t("అసలు వడ్డీ", "Interest without prepayment"), rupees(it.originalInterest))
                ResultRow(language.t("కొత్త వడ్డీ", "Interest with prepayment"), rupees(it.newInterest))
            }
        }
    )
}
