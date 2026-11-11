package com.example.utilities.tools

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.ChitFundCalculator
import com.example.utilities.calc.ChitResult
import com.example.utilities.calc.IndianFormat
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.rupees
import com.example.utilities.ui.t
import kotlin.math.abs

class ChitViewModel : ToolViewModel<ChitViewModel.Input>(Input()) {
    data class Input(
        val value: String = "",
        val members: String = "20",
        val months: String = "20",
        val commission: String = "5",
        val bid: String = "",
        val month: String = "1"
    )

    fun result(input: Input): ChitResult? {
        val value = num(input.value) ?: return null
        val members = num(input.members)?.toInt() ?: return null
        val months = num(input.months)?.toInt() ?: return null
        val commission = num(input.commission) ?: return null
        val bid = num(input.bid) ?: return null
        val month = num(input.month)?.toInt() ?: return null
        if (value <= 0 || members !in 2..200 || months !in 2..200 || month !in 1..months || bid !in 0.0..value) return null
        return ChitFundCalculator.calculate(value, members, months, commission, bid, month)
    }
}

@Composable
fun ChitScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: ChitViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }

    fun rateText(r: ChitResult): String {
        val rate = r.annualRatePercent ?: return language.t("లెక్కించలేము", "Can't be worked out")
        val earning = r.profit >= 0
        return "${IndianFormat.number(abs(rate))}% " + if (earning) language.t("(మీకు లాభం)", "(you earn)") else language.t("(మీకు ఖర్చు)", "(it costs you)")
    }

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        note = ToolNote.FINANCE,
        shareText = result?.let {
            "${language.t("వచ్చే మొత్తం", "Amount received")}: ${rupees(it.amountReceived)}\n" +
                "${language.t("మొత్తం కట్టినది", "Total paid")}: ${rupees(it.totalPaid)}\n" +
                "${language.t("లాభం / నష్టం", "Profit / loss")}: ${rupees(it.profit)}\n" +
                "${language.t("సుమారు వడ్డీ", "Approx. rate")}: ${rateText(it)}"
        },
        inputs = {
            ToolNumberField(language.t("చిట్టీ విలువ", "Chit value"), input.value, { v -> vm.update { it.copy(value = v) } }, suffix = "₹")
            ToolNumberField(language.t("సభ్యులు", "Members"), input.members, { v -> vm.update { it.copy(members = v) } }, decimal = false)
            ToolNumberField(language.t("నెలలు", "Months"), input.months, { v -> vm.update { it.copy(months = v) } }, decimal = false)
            ToolNumberField(language.t("ఫోర్‌మెన్ కమీషన్", "Foreman commission"), input.commission, { v -> vm.update { it.copy(commission = v) } }, suffix = "%")
            ToolNumberField(language.t("పాట (బిడ్) మొత్తం", "Bid amount"), input.bid, { v -> vm.update { it.copy(bid = v) } }, suffix = "₹")
            ToolNumberField(language.t("మీరు పాడిన నెల", "Month you take the chit"), input.month, { v -> vm.update { it.copy(month = v) } }, decimal = false)
            Text(
                language.t("ప్రతి నెలా ఇదే పాట అని అనుకుని లెక్కిస్తాం.", "Assumes every month's auction has this same bid."),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        result = result?.let {
            {
                ResultHeadline(language.t("మీకు వచ్చే మొత్తం", "Amount you receive"), rupees(it.amountReceived))
                ResultRow(language.t("నెలవారీ వాయిదా", "Monthly instalment"), rupees(it.monthlyContribution))
                ResultRow(language.t("నెలవారీ డివిడెండ్", "Dividend per month"), rupees(it.dividendPerMonth))
                ResultRow(language.t("కమీషన్ (నెలకు)", "Commission per auction"), rupees(it.commissionPerMonth))
                ResultRow(language.t("మొత్తం కట్టినది", "Total paid"), rupees(it.totalPaid))
                ResultRow(language.t("లాభం / నష్టం", "Profit / loss"), rupees(it.profit))
                ResultRow(language.t("సుమారు సంవత్సర వడ్డీ", "Approx. yearly rate"), rateText(it))
            }
        }
    )
}
