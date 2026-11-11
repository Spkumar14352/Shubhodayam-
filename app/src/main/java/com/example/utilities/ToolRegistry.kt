package com.example.utilities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.Language
import com.example.i18n.Translations

enum class ToolCategory(val en: String, val te: String) {
    MONEY("Money", "డబ్బు"),
    DAILY("Daily", "రోజువారీ"),
    HEALTH("Health", "ఆరోగ్యం"),
    DOCUMENTS("Documents", "డాక్యుమెంట్స్"),
    BHAKTI("Bhakti", "భక్తి"),
    CONVERTERS("Converters", "కన్వర్టర్లు")
}

/** Name and description come from Translations keys "tool_<id>_name" and "tool_<id>_desc". */
data class UtilityTool(
    val id: String,
    val category: ToolCategory,
    val icon: ImageVector,
    /** Extra search words in either language. */
    val keywords: List<String> = emptyList()
) {
    fun name(language: Language) = Translations.tr("tool_${id}_name", language)
    fun description(language: Language) = Translations.tr("tool_${id}_desc", language)
}

object ToolRegistry {
    val tools = listOf(
        UtilityTool("emi", ToolCategory.MONEY, Icons.Default.AccountBalance, listOf("loan", "home loan", "schedule", "planner", "రుణం", "లోన్")),
        UtilityTool("gst", ToolCategory.MONEY, Icons.Default.Receipt, listOf("tax", "cgst", "sgst", "igst", "split", "పన్ను")),
        UtilityTool("percentage", ToolCategory.MONEY, Icons.Default.Percent, listOf("percent", "discount", "offer", "sale", "తగ్గింపు", "శాతం")),
        UtilityTool("vaddi", ToolCategory.MONEY, Icons.Default.CurrencyRupee, listOf("interest", "simple", "compound", "వడ్డీ", "అప్పు")),
        UtilityTool("chit", ToolCategory.MONEY, Icons.Default.Groups, listOf("chit fund", "bid", "auction", "చిట్టీ", "చీటీ")),
        UtilityTool("loan_prepayment", ToolCategory.MONEY, Icons.AutoMirrored.Filled.TrendingDown, listOf("prepay", "part payment", "ముందస్తు చెల్లింపు")),
        UtilityTool("ppf", ToolCategory.MONEY, Icons.Default.Savings, listOf("public provident fund", "tax saving", "పొదుపు")),
        UtilityTool("ssy", ToolCategory.MONEY, Icons.Default.Face, listOf("sukanya", "girl", "daughter", "సుకన్య", "అమ్మాయి")),
        UtilityTool("epf", ToolCategory.MONEY, Icons.Default.Work, listOf("pf", "provident fund", "retirement", "ఉద్యోగ")),
        UtilityTool("number_words", ToolCategory.MONEY, Icons.Default.TextFields, listOf("cheque", "amount in words", "lakh", "crore", "అక్షరాల్లో", "చెక్కు")),
        UtilityTool("bmi", ToolCategory.HEALTH, Icons.Default.MonitorWeight, listOf("weight", "height", "healthy weight", "బరువు", "ఎత్తు")),
        UtilityTool("unit_converter", ToolCategory.CONVERTERS, Icons.Default.Straighten, listOf("length", "weight", "tola", "acre", "cent", "temperature", "తులం", "ఎకరం", "కొలతలు"))
    )

    /** Ids of tools that were merged, so saved recents, favourites and old links still open the right screen. */
    val aliases = mapOf(
        "emi_planner" to "emi",
        "emi_calculator" to "emi",
        "gst_split" to "gst",
        "gst_calculator" to "gst",
        "bmi_healthy_weight" to "bmi",
        "bmi_calculator" to "bmi",
        "percent" to "percentage",
        "discount" to "percentage",
        "discount_percentage" to "percentage"
    )

    fun resolve(id: String): UtilityTool? = tools.firstOrNull { it.id == (aliases[id] ?: id) }

    fun search(query: String): List<UtilityTool> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return tools
        return tools.filter { tool ->
            val haystack = listOf(
                tool.name(Language.ENGLISH), tool.name(Language.TELUGU),
                tool.description(Language.ENGLISH), tool.description(Language.TELUGU),
                tool.category.en, tool.category.te
            ) + tool.keywords
            haystack.any { it.lowercase().contains(q) }
        }
    }
}
