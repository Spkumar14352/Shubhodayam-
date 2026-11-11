package com.example.utilities.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.BmiCalculator
import com.example.utilities.calc.BmiResult
import com.example.utilities.calc.IndianFormat
import com.example.utilities.ui.ResultHeadline
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNote
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.t
import kotlin.math.abs

class BmiViewModel : ToolViewModel<BmiViewModel.Input>(Input()) {
    data class Input(val weight: String = "", val useFeet: Boolean = true, val cm: String = "", val feet: String = "", val inches: String = "")

    fun result(input: Input): BmiResult? {
        val weight = num(input.weight) ?: return null
        val height = if (input.useFeet) {
            BmiCalculator.heightCm(num(input.feet) ?: return null, num(input.inches) ?: 0.0)
        } else {
            num(input.cm) ?: return null
        }
        if (weight !in 2.0..400.0 || height !in 50.0..260.0) return null
        return BmiCalculator.calculate(weight, height)
    }
}

/** BMI & Healthy Weight: replaces the separate BMI and BMI-and-Healthy-Weight tools. */
@Composable
fun BmiScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: BmiViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    fun kg(value: Double) = "${IndianFormat.number(value, 1)} kg"

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        note = ToolNote.HEALTH,
        shareText = result?.let {
            "BMI: ${IndianFormat.number(it.bmi, 1)} (${language.t(it.category.te, it.category.en)})\n" +
                language.t("ఆరోగ్యకరమైన బరువు", "Healthy weight") + ": ${kg(it.healthyMinKg)} – ${kg(it.healthyMaxKg)}"
        },
        inputs = {
            ToolNumberField(language.t("బరువు", "Weight"), input.weight, { v -> vm.update { it.copy(weight = v) } }, suffix = "kg")
            ToolChoices(listOf(true to language.t("అడుగులు + అంగుళాలు", "Feet + inches"), false to language.t("సెంటీమీటర్లు", "Centimetres")), input.useFeet) { v -> vm.update { it.copy(useFeet = v) } }
            if (input.useFeet) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToolNumberField(language.t("అడుగులు", "Feet"), input.feet, { v -> vm.update { it.copy(feet = v) } }, modifier = Modifier.weight(1f), suffix = "ft")
                    ToolNumberField(language.t("అంగుళాలు", "Inches"), input.inches, { v -> vm.update { it.copy(inches = v) } }, modifier = Modifier.weight(1f), suffix = "in")
                }
            } else {
                ToolNumberField(language.t("ఎత్తు", "Height"), input.cm, { v -> vm.update { it.copy(cm = v) } }, suffix = "cm")
            }
        },
        result = result?.let {
            {
                ResultHeadline("BMI", "${IndianFormat.number(it.bmi, 1)} • ${language.t(it.category.te, it.category.en)}")
                ResultRow(language.t("ఆరోగ్యకరమైన బరువు", "Healthy weight for your height"), "${kg(it.healthyMinKg)} – ${kg(it.healthyMaxKg)}")
                when {
                    it.changeToHealthyKg > 0 -> ResultRow(language.t("తగ్గవలసినది", "To reach the healthy range, lose"), kg(it.changeToHealthyKg))
                    it.changeToHealthyKg < 0 -> ResultRow(language.t("పెరగవలసినది", "To reach the healthy range, gain"), kg(abs(it.changeToHealthyKg)))
                    else -> ResultRow(language.t("స్థితి", "Status"), language.t("ఆరోగ్యకరమైన పరిధిలో ఉన్నారు", "Within the healthy range"))
                }
                ResultRow(
                    language.t("గమనిక", "Note"),
                    language.t("భారతీయులకు వైద్యులు 23 నుండే అధిక బరువుగా చూస్తారు", "Doctors often treat 23+ as overweight for Indians")
                )
            }
        }
    )
}
