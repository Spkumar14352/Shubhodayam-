package com.example.utilities.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.IndianFormat
import com.example.utilities.calc.PercentageCalculator
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t

enum class PercentMode { PERCENT_OF, WHAT_PERCENT, CHANGE, DISCOUNT }

class PercentageViewModel : ToolViewModel<PercentageViewModel.Input>(Input()) {
    data class Input(val mode: PercentMode = PercentMode.DISCOUNT, val a: String = "", val b: String = "", val c: String = "")

    /** Headline plus detail rows, or null while inputs are incomplete. */
    fun result(input: Input, language: Language): Pair<String, List<Pair<String, String>>>? {
        val a = num(input.a) ?: return null
        val b = num(input.b) ?: return null
        fun n(x: Double) = IndianFormat.number(x)
        return when (input.mode) {
            PercentMode.PERCENT_OF -> n(PercentageCalculator.percentOf(a, b)) to emptyList()
            PercentMode.WHAT_PERCENT -> if (b == 0.0) null else "${n(PercentageCalculator.whatPercent(a, b))}%" to emptyList()
            PercentMode.CHANGE -> if (a == 0.0) null else {
                val change = PercentageCalculator.percentChange(a, b)
                "${if (change >= 0) "+" else ""}${n(change)}%" to listOf(language.t("తేడా", "Difference") to n(b - a))
            }
            PercentMode.DISCOUNT -> {
                val d = PercentageCalculator.discount(a, b, num(input.c) ?: 0.0)
                rupees(d.finalPrice, 2) to listOf(
                    language.t("ఆదా", "You save") to rupees(d.saved, 2),
                    language.t("మొత్తం తగ్గింపు", "Total discount") to "${n(d.effectiveDiscountPercent)}%"
                )
            }
        }
    }
}

/** Percentage & Discount: replaces the separate Percent and Discount-and-Percentage tools. */
@Composable
fun PercentageScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: PercentageViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input, language) { vm.result(input, language) }
    val (labelA, labelB) = when (input.mode) {
        PercentMode.PERCENT_OF -> language.t("శాతం (%)", "Percent (%)") to language.t("దేనిలో", "Of value")
        PercentMode.WHAT_PERCENT -> language.t("భాగం", "Part") to language.t("మొత్తం", "Whole")
        PercentMode.CHANGE -> language.t("పాత విలువ", "Old value") to language.t("కొత్త విలువ", "New value")
        PercentMode.DISCOUNT -> language.t("అసలు ధర", "Original price") to language.t("తగ్గింపు (%)", "Discount (%)")
    }

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        shareText = result?.let { (headline, rows) -> (listOf(headline) + rows.map { "${it.first}: ${it.second}" }).joinToString("\n") },
        inputs = {
            ToolChoices(
                listOf(
                    PercentMode.DISCOUNT to language.t("తగ్గింపు", "Discount"),
                    PercentMode.PERCENT_OF to language.t("X% అంటే ఎంత", "X% of Y"),
                    PercentMode.WHAT_PERCENT to language.t("ఎంత శాతం", "X is what % of Y"),
                    PercentMode.CHANGE to language.t("శాతం మార్పు", "% change")
                ),
                input.mode
            ) { v -> vm.update { it.copy(mode = v) } }
            ToolNumberField(labelA, input.a, { v -> vm.update { it.copy(a = v) } })
            ToolNumberField(labelB, input.b, { v -> vm.update { it.copy(b = v) } })
            if (input.mode == PercentMode.DISCOUNT) {
                ToolNumberField(language.t("అదనపు తగ్గింపు (%) – ఐచ్ఛికం", "Extra discount (%) – optional"), input.c, { v -> vm.update { it.copy(c = v) } })
            }
        },
        result = result?.let { (headline, rows) ->
            {
                ResultHeadline(if (input.mode == PercentMode.DISCOUNT) language.t("చెల్లించవలసింది", "You pay") else language.t("ఫలితం", "Result"), headline)
                rows.forEach { ResultRow(it.first, it.second) }
            }
        }
    )
}
