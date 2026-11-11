package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.GstCalculator
import com.example.utilities.calc.GstResult
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t

class GstViewModel : ToolViewModel<GstViewModel.Input>(Input()) {
    data class Input(val amount: String = "", val rate: String = "18", val inclusive: Boolean = false, val interState: Boolean = false)

    fun result(input: Input): GstResult? {
        val amount = num(input.amount) ?: return null
        val rate = num(input.rate) ?: return null
        if (amount < 0 || rate !in 0.0..100.0) return null
        return GstCalculator.calculate(amount, rate, input.inclusive)
    }
}

/** GST Calculator: replaces the separate GST and GST Split tools. */
@Composable
fun GstScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: GstViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        note = ToolNote.FINANCE,
        shareText = result?.let {
            val split = if (input.interState) "IGST: ${rupees(it.igst, 2)}" else "CGST: ${rupees(it.cgst, 2)}, SGST: ${rupees(it.sgst, 2)}"
            "${language.t("అసలు ధర", "Base")}: ${rupees(it.baseAmount, 2)}\nGST ${input.rate}%: ${rupees(it.gstAmount, 2)} ($split)\n${language.t("మొత్తం", "Total")}: ${rupees(it.totalAmount, 2)}"
        },
        inputs = {
            ToolNumberField(language.t("మొత్తం", "Amount"), input.amount, { v -> vm.update { it.copy(amount = v) } }, suffix = "₹")
            ToolChoices(
                listOf(false to language.t("GST కలపాలి", "Add GST (exclusive)"), true to language.t("GST కలిసి ఉంది", "GST included (inclusive)")),
                input.inclusive
            ) { v -> vm.update { it.copy(inclusive = v) } }
            ToolChoices(GstCalculator.COMMON_RATES.map { r -> "${r.toInt()}" to "${r.toInt()}%" }, input.rate) { v -> vm.update { it.copy(rate = v) } }
            ToolNumberField(language.t("GST రేటు", "GST rate"), input.rate, { v -> vm.update { it.copy(rate = v) } }, suffix = "%")
            ToolChoices(
                listOf(false to language.t("రాష్ట్రంలోనే (CGST + SGST)", "Same state (CGST + SGST)"), true to language.t("ఇతర రాష్ట్రం (IGST)", "Other state (IGST)")),
                input.interState
            ) { v -> vm.update { it.copy(interState = v) } }
        },
        result = result?.let {
            {
                ResultHeadline(language.t("మొత్తం (GST తో)", "Total with GST"), rupees(it.totalAmount, 2))
                ResultRow(language.t("అసలు ధర", "Base amount"), rupees(it.baseAmount, 2))
                ResultRow("GST", rupees(it.gstAmount, 2))
                if (input.interState) {
                    ResultRow("IGST", rupees(it.igst, 2))
                } else {
                    ResultRow("CGST (${input.rate.toDoubleOrNull()?.div(2) ?: ""}%)", rupees(it.cgst, 2))
                    ResultRow("SGST (${input.rate.toDoubleOrNull()?.div(2) ?: ""}%)", rupees(it.sgst, 2))
                }
            }
        }
    )
}
