@file:OptIn(ExperimentalFoundationApi::class)

package com.example.utilities

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.i18n.Translations
import com.example.screens.SubScreenHeader
import com.example.utilities.tools.BmiScreen
import com.example.utilities.tools.ChitScreen
import com.example.utilities.tools.EmiScreen
import com.example.utilities.tools.EpfScreen
import com.example.utilities.tools.GstScreen
import com.example.utilities.tools.LoanPrepaymentScreen
import com.example.utilities.tools.NumberWordsScreen
import com.example.utilities.tools.PercentageScreen
import com.example.utilities.tools.PpfScreen
import com.example.utilities.tools.SsyScreen
import com.example.utilities.tools.UnitConverterScreen
import com.example.utilities.tools.VaddiScreen
import com.example.utilities.ui.t

@Composable
fun UtilitiesScreen(language: Language, onBack: () -> Unit, initialToolId: String? = null) {
    val vm: UtilitiesViewModel = viewModel()
    val state by vm.state.collectAsState()
    var openId by rememberSaveable { mutableStateOf(initialToolId) }
    fun open(id: String) {
        vm.opened(id)
        openId = id
    }

    // Old ids (e.g. "emi_planner") resolve to the merged tool.
    val openTool = openId?.let(ToolRegistry::resolve)
    if (openTool != null) {
        ToolHost(openTool, language) { openId = null }
        return
    }

    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("utilities_screen")
    ) {
        SubScreenHeader(title = Translations.tr("menu_utilities", language), onBack = onBack)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = vm::setQuery,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text(language.t("ఉపకరణం వెతకండి (తెలుగు / English)", "Search tools (English / తెలుగు)")) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utilities_search")
                )
            }

            if (state.query.isBlank()) {
                if (state.recents.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ToolRow(language.t("ఇటీవల వాడినవి", "Recently used"), state.recents, language, ::open)
                    }
                }
                val favorites = ToolRegistry.tools.filter { it.id in state.favorites }
                if (favorites.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ToolRow(language.t("ఇష్టమైనవి", "Favorites"), favorites, language, ::open)
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val tabs = listOf<ToolCategory?>(null) + ToolCategory.entries
                    ScrollableTabRow(
                        selectedTabIndex = tabs.indexOf(state.category),
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        tabs.forEach { category ->
                            Tab(
                                selected = category == state.category,
                                onClick = { vm.setCategory(category) },
                                text = { Text(category?.let { language.t(it.te, it.en) } ?: language.t("అన్నీ", "All")) },
                                modifier = Modifier.testTag("utilities_tab_${category?.name ?: "ALL"}")
                            )
                        }
                    }
                }
            }

            if (state.visibleTools.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = if (state.query.isNotBlank()) {
                            language.t("ఏ ఉపకరణమూ దొరకలేదు.", "No tools match your search.")
                        } else {
                            language.t("ఈ విభాగంలో మరిన్ని ఉపకరణాలు త్వరలో.", "More tools in this section are coming soon.")
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(state.visibleTools, key = { it.id }) { tool ->
                ToolCardItem(
                    tool = tool,
                    language = language,
                    isFavorite = tool.id in state.favorites,
                    onOpen = { open(tool.id) },
                    onToggleFavorite = { vm.toggleFavorite(tool.id) }
                )
            }
        }
    }
}

@Composable
private fun ToolRow(title: String, tools: List<UtilityTool>, language: Language, onOpen: (String) -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tools, key = { it.id }) { tool ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpen(tool.id) }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(tool.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(tool.name(language), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCardItem(
    tool: UtilityTool,
    language: Language,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onOpen, onLongClick = onToggleFavorite)
            .testTag("tool_card_${tool.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tool.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp).testTag("tool_star_${tool.id}")) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = language.t("ఇష్టమైనవి", "Favorite"),
                        tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                tool.name(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                tool.description(language),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Opens one tool's screen. */
@Composable
fun ToolHost(tool: UtilityTool, language: Language, onBack: () -> Unit) {
    val title = tool.name(language)
    when (tool.id) {
        "emi" -> EmiScreen(language, title, onBack)
        "gst" -> GstScreen(language, title, onBack)
        "percentage" -> PercentageScreen(language, title, onBack)
        "vaddi" -> VaddiScreen(language, title, onBack)
        "chit" -> ChitScreen(language, title, onBack)
        "loan_prepayment" -> LoanPrepaymentScreen(language, title, onBack)
        "ppf" -> PpfScreen(language, title, onBack)
        "ssy" -> SsyScreen(language, title, onBack)
        "epf" -> EpfScreen(language, title, onBack)
        "number_words" -> NumberWordsScreen(language, title, onBack)
        "bmi" -> BmiScreen(language, title, onBack)
        "unit_converter" -> UnitConverterScreen(language, title, onBack)
        else -> error("No screen for tool ${tool.id}")
    }
}
