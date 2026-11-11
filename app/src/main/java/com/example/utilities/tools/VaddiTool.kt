package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.IndianFormat
import com.example.utilities.calc.VaddiCalculator
import com.example.utilities.calc.VaddiRateMode
import com.example.utilities.calc.VaddiResult
import com.example.utilities.calc.VaddiType
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

class VaddiViewModel : ToolViewModel<VaddiViewModel.Input>(Input()) {
    data class Input(
        val principal: String = "",
        val rate: String = "2",
        val mode: VaddiRateMode = VaddiRateMode.RUPEES_PER_100_PER_MONTH,
        val months: String = "12",
        val type: VaddiType = VaddiType.SIMPLE
    )

    fun result(input: Input): VaddiResult? {
        val principal = num(input.principal) ?: return null
        val rate = num(input.rate) ?: return null
        val months = num(input.months)?.toInt() ?: return null
        if (principal <= 0 || rate < 0 || months !in 1..600) return null
        return VaddiCalculator.calculate(principal, rate, input.mode, months, input.type)
    }
}

@Composable
fun VaddiScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: VaddiViewModel = viewModel()
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
                "అసలు: ${rupees(num(input.principal) ?: 0.0)}\nవడ్డీ: ${IndianFormat.number(it.annualPercent)}% సంవత్సరానికి\nమొత్తం వడ్డీ: ${rupees(it.totalInterest)}\nచెల్లించవలసింది: ${rupees(it.totalPayable)}",
                "Principal: ${rupees(num(input.principal) ?: 0.0)}\nRate: ${IndianFormat.number(it.annualPercent)}% a year\nTotal interest: ${rupees(it.totalInterest)}\nTotal payable: ${rupees(it.totalPayable)}"
            )
        },
        inputs = {
            ToolNumberField(language.t("అసలు మొత్తం", "Principal"), input.principal, { v -> vm.update { it.copy(principal = v) } }, suffix = "₹")
            ToolChoices(
                listOf(
                    VaddiRateMode.RUPEES_PER_100_PER_MONTH to language.t("₹100 కి నెలకు రూపాయలు", "₹ per ₹100 per month"),
                    VaddiRateMode.PERCENT_PER_YEAR to language.t("సంవత్సరానికి %", "% per year")
                ),
                input.mode
            ) { v -> vm.update { it.copy(mode = v) } }
            ToolNumberField(
                if (input.mode == VaddiRateMode.RUPEES_PER_100_PER_MONTH) language.t("వడ్డీ (ఉదా: 2 రూపాయల వడ్డీ)", "Rate (e.g. ₹2 vaddi)") else language.t("వడ్డీ రేటు", "Interest rate"),
                input.rate,
                { v -> vm.update { it.copy(rate = v) } },
                suffix = if (input.mode == VaddiRateMode.RUPEES_PER_100_PER_MONTH) "₹" else "%"
            )
            ToolNumberField(language.t("నెలలు", "Months"), input.months, { v -> vm.update { it.copy(months = v) } }, decimal = false)
            ToolChoices(
                listOf(
                    VaddiType.SIMPLE to language.t("సాధారణ వడ్డీ", "Simple"),
                    VaddiType.COMPOUND_MONTHLY to language.t("చక్రవడ్డీ (నెలవారీ)", "Compound monthly"),
                    VaddiType.COMPOUND_YEARLY to language.t("చక్రవడ్డీ (సంవత్సరానికి)", "Compound yearly")
                ),
                input.type
            ) { v -> vm.update { it.copy(type = v) } }
        },
        result = result?.let {
            {
                ResultHeadline(language.t("మొత్తం చెల్లించవలసింది", "Total payable"), rupees(it.totalPayable))
                ResultRow(language.t("మొత్తం వడ్డీ", "Total interest"), rupees(it.totalInterest))
                ResultRow(language.t("సంవత్సర వడ్డీ రేటు", "Yearly rate"), "${IndianFormat.number(it.annualPercent)}%")
            }
        },
        extra = result?.let {
            {
                ToolTable(
                    title = language.t("నెలవారీ పట్టిక", "Month by month"),
                    headers = listOf(language.t("నెల", "Month"), language.t("ఈ నెల వడ్డీ", "Interest"), language.t("ఇప్పటివరకు వడ్డీ", "Interest so far"), language.t("బాకీ", "Amount due")),
                    rows = it.rows.map { row -> listOf(row.month.toString(), rupees(row.interestThisMonth), rupees(row.interestSoFar), rupees(row.amountDue)) }
                )
            }
        }
    )
}
