package com.example.utilities.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.IndianFormat
import com.example.utilities.calc.UnitCategory
import com.example.utilities.calc.UnitConverter
import com.example.utilities.ui.ResultRow
import com.example.utilities.ui.ToolChoices
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.t
import kotlin.math.abs

class UnitConverterViewModel : ToolViewModel<UnitConverterViewModel.Input>(Input()) {
    data class Input(
        val category: UnitCategory = UnitCategory.LENGTH,
        val from: String = "ft",
        val value: String = "",
        val inches: String = ""
    )

    fun selectCategory(category: UnitCategory) =
        update { it.copy(category = category, from = UnitConverter.units.getValue(category).first().id, value = "", inches = "") }

    /** Value in the "from" unit, combining feet and inches for the feet + inches unit. */
    fun amount(input: Input): Double? {
        val value = num(input.value) ?: return null
        return if (input.from == UnitConverter.FOOT_INCH) UnitConverter.feetInches(value, num(input.inches) ?: 0.0) else value
    }

    /** Every unit in the category with the converted value. */
    fun results(input: Input, language: Language): List<Pair<String, String>>? {
        val amount = amount(input) ?: return null
        val fromId = if (input.from == UnitConverter.FOOT_INCH) "ft" else input.from
        return UnitConverter.units.getValue(input.category).filter { it.id != input.from }.map { unit ->
            val label = language.t(unit.te, unit.en)
            if (unit.id == UnitConverter.FOOT_INCH) {
                val (feet, inches) = UnitConverter.toFeetInches(UnitConverter.convert(amount, input.category, fromId, "ft"))
                label to "$feet ft ${IndianFormat.number(inches, 1)} in"
            } else {
                label to format(UnitConverter.convert(amount, input.category, fromId, unit.id))
            }
        }
    }

    private fun format(value: Double): String = when {
        value == 0.0 -> "0"
        abs(value) >= 0.01 -> IndianFormat.number(value, 4).trimEnd('0').trimEnd('.')
        else -> "%.4g".format(value)
    }
}

@Composable
fun UnitConverterScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: UnitConverterViewModel = viewModel()
    val input by vm.state.collectAsState()
    val results = remember(input, language) { vm.results(input, language) }
    val fromUnit = UnitConverter.unit(input.category, input.from)

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        shareText = results?.let { rows ->
            val fromText = if (input.from == UnitConverter.FOOT_INCH) "${input.value} ft ${input.inches.ifBlank { "0" }} in" else "${input.value} ${language.t(fromUnit.te, fromUnit.en)}"
            (listOf("$fromText =") + rows.map { "${it.second} ${it.first}" }).joinToString("\n")
        },
        inputs = {
            ToolChoices(UnitCategory.entries.map { it to language.t(it.te, it.en) }, input.category, vm::selectCategory)
            ToolChoices(UnitConverter.units.getValue(input.category).map { it.id to language.t(it.te, it.en) }, input.from) { v -> vm.update { it.copy(from = v) } }
            if (input.from == UnitConverter.FOOT_INCH) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToolNumberField(language.t("అడుగులు", "Feet"), input.value, { v -> vm.update { it.copy(value = v) } }, modifier = Modifier.weight(1f), suffix = "ft")
                    ToolNumberField(language.t("అంగుళాలు", "Inches"), input.inches, { v -> vm.update { it.copy(inches = v) } }, modifier = Modifier.weight(1f), suffix = "in")
                }
            } else {
                // Temperatures can be negative.
                if (input.category == UnitCategory.TEMPERATURE) {
                    OutlinedTextField(
                        value = input.value,
                        onValueChange = { text -> vm.update { it.copy(value = text.filter { c -> c.isDigit() || c == '.' || c == '-' }) } },
                        label = { Text(language.t(fromUnit.te, fromUnit.en)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ToolNumberField(language.t(fromUnit.te, fromUnit.en), input.value, { v -> vm.update { it.copy(value = v) } })
                }
            }
        },
        result = results?.let { rows -> { rows.forEach { ResultRow(it.first, it.second) } } }
    )
}
