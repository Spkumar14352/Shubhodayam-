package com.example.utilities.tools

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.utilities.ToolViewModel
import com.example.utilities.calc.IndianFormat
import com.example.utilities.calc.NumberToWords
import com.example.utilities.ui.ToolNumberField
import com.example.utilities.ui.ToolScaffold
import com.example.utilities.ui.num
import com.example.utilities.ui.t

class NumberWordsViewModel : ToolViewModel<NumberWordsViewModel.Input>(Input()) {
    data class Input(val amount: String = "")

    /** English words, English cheque line, Telugu words, Telugu cheque line. */
    fun result(input: Input): List<String>? {
        val amount = num(input.amount) ?: return null
        if (amount < 0 || amount >= MAX) return null
        val whole = amount.toLong()
        return listOf(
            NumberToWords.english(whole),
            NumberToWords.rupeesEnglish(amount),
            NumberToWords.telugu(whole),
            NumberToWords.rupeesTelugu(amount)
        )
    }

    companion object {
        /** Up to 999 crore; larger numbers are rarely written on cheques. */
        const val MAX = 1e10
    }
}

@Composable
fun NumberWordsScreen(language: Language, title: String, onBack: () -> Unit) {
    val vm: NumberWordsViewModel = viewModel()
    val input by vm.state.collectAsState()
    val result = remember(input) { vm.result(input) }
    val clipboard = LocalClipboardManager.current
    val labels = listOf(
        language.t("ఇంగ్లీష్‌లో", "In English"),
        language.t("చెక్కు కోసం (ఇంగ్లీష్)", "For cheques (English)"),
        language.t("తెలుగులో", "In Telugu"),
        language.t("చెక్కు కోసం (తెలుగు)", "For cheques (Telugu)")
    )

    ToolScaffold(
        title = title,
        language = language,
        onBack = onBack,
        onReset = vm::reset,
        shareText = result?.let { "${IndianFormat.rupees(num(input.amount) ?: 0.0, 2)}\n${it[1]}\n${it[3]}" },
        inputs = {
            ToolNumberField(language.t("మొత్తం", "Amount"), input.amount, { v -> vm.update { it.copy(amount = v) } }, suffix = "₹")
            num(input.amount)?.let { Text(IndianFormat.rupees(it, 2), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) }
        },
        result = result?.let { words ->
            {
                words.forEachIndexed { index, text ->
                    Text(labels[index], fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        IconButton(onClick = { clipboard.setText(AnnotatedString(text)) }, modifier = Modifier.testTag("copy_words_$index")) {
                            Icon(Icons.Default.ContentCopy, contentDescription = language.t("కాపీ", "Copy"), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    )
}
