@file:OptIn(ExperimentalLayoutApi::class)

package com.example.utilities.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.screens.SubScreenHeader

enum class ToolNote { FINANCE, HEALTH }

/**
 * Shared layout for every utility: top bar, a card of inputs, a result card, then Share and Reset.
 * [extra] holds anything below the buttons, such as a schedule table.
 */
@Composable
fun ToolScaffold(
    title: String,
    language: Language,
    onBack: () -> Unit,
    onReset: () -> Unit,
    shareText: String?,
    note: ToolNote? = null,
    inputs: @Composable ColumnScope.() -> Unit,
    result: (@Composable ColumnScope.() -> Unit)?,
    extra: (@Composable ColumnScope.() -> Unit)? = null
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val isTelugu = language == Language.TELUGU

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("tool_scaffold")
    ) {
        SubScreenHeader(title = title, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            note?.let {
                Text(
                    text = when (it) {
                        ToolNote.FINANCE -> if (isTelugu) {
                            "సాధారణ మార్గదర్శకం కోసం మాత్రమే. అసలు మొత్తాలు మారవచ్చు – మీ బ్యాంకు లేదా సలహాదారుతో నిర్ధారించుకోండి."
                        } else {
                            "For general guidance only. Actual figures may differ – confirm with your bank or adviser."
                        }
                        ToolNote.HEALTH -> if (isTelugu) {
                            "సాధారణ మార్గదర్శకం కోసం మాత్రమే. ఇది వైద్య సలహా కాదు – వైద్యుడిని సంప్రదించండి."
                        } else {
                            "For general guidance only. This is not medical advice – please consult a doctor."
                        }
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("tool_note")
                )
            }

            ToolCard { inputs() }

            if (result != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tool_result"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { result() }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val text = shareText ?: return@Button
                        context.startActivity(
                            Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text), title)
                        )
                    },
                    enabled = shareText != null,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tool_share"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTelugu) "ఫలితం షేర్" else "Share result")
                }
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tool_reset"),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTelugu) "రీసెట్" else "Reset", color = MaterialTheme.colorScheme.onSurface)
                }
            }

            extra?.invoke(this)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ToolCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { content() }
    }
}

@Composable
fun ToolNumberField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    decimal: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { text -> onValue(text.filter { it.isDigit() || it == '.' || it == ',' }) },
        label = { Text(label) },
        suffix = suffix?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun <T> ToolChoices(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = value == selected,
                onClick = { onSelect(value) },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

/** The main answer, shown large. */
@Composable
fun ResultHeadline(label: String, value: String) {
    Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.testTag("tool_headline"))
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** A simple scrollable table for schedules. */
@Composable
fun ToolTable(title: String, headers: List<String>, rows: List<List<String>>) {
    ToolCard {
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Row { headers.forEach { TableCell(it, header = true) } }
            rows.forEach { row -> Row { row.forEach { TableCell(it, header = false) } } }
        }
    }
}

@Composable
private fun TableCell(text: String, header: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .width(104.dp)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        fontSize = 12.sp,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        color = if (header) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
}
