package com.example.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.AiPanditMessage
import com.example.R
import com.example.data.festivals.FestivalCalendar
import java.time.LocalDate
import com.example.data.model.City
import com.example.data.model.CityData
import com.example.data.model.Festival
import com.example.data.model.Language
import com.example.data.model.MediaSampleData
import com.example.data.model.RadioStation
import com.example.data.model.Rasi
import com.example.data.model.RasiData
import com.example.data.model.ThemeMode
import com.example.i18n.Translations
import androidx.compose.material.icons.filled.Refresh
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldDark
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleMaroonLight
import com.example.ui.theme.TempleSaffron

// Sub-screen header with Back button
@Composable
fun SubScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TempleMaroonDark,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("subscreen_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TempleGoldLight
                )
            }
            Text(
                text = title,
                color = TempleGoldLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 1. Radio Sub-Screen (Radio Browser API)
@Composable
fun RadioSubScreen(
    language: Language,
    selectedCategory: String, // "TELUGU", "DEVOTIONAL", "NATIONAL"
    stations: List<com.example.data.model.RadioBrowserStation>,
    isLoading: Boolean,
    playerState: com.example.services.RadioPlayerState,
    onSelectCategory: (String) -> Unit,
    onSelectStation: (com.example.data.model.RadioBrowserStation) -> Unit,
    onTogglePlay: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val categories = listOf(
        Pair("TELUGU", Translations.tr("radio_telugu_tab", language)),
        Pair("DEVOTIONAL", Translations.tr("radio_devotional_tab", language)),
        Pair("NATIONAL", Translations.tr("radio_national_tab", language))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("radio_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("radio_title", language), onBack = onBack)

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (catKey, label) ->
                val isSelected = selectedCategory == catKey
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectCategory(catKey) }
                        .testTag("radio_cat_$catKey"),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) TempleSaffron else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) TempleGold else Color.Transparent)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Active Player Card (if a station is loaded)
        val currentStation = playerState.currentStation
        if (currentStation != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TempleMaroonDark),
                border = BorderStroke(1.dp, TempleGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(TempleGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentStation.getInitials(),
                            color = TempleMaroonDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentStation.name,
                            color = TempleGoldLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (currentStation.tags.isNotBlank()) currentStation.tags.take(30) else "Live Radio Stream",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (playerState.isLoading) {
                            Text(
                                text = Translations.tr("radio_connecting", language),
                                color = TempleSaffron,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TempleGold)
                            .testTag("radio_play_pause_btn")
                    ) {
                        if (playerState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = TempleMaroonDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = TempleMaroonDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stations List / Loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = TempleGold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Translations.tr("radio_connecting", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else if (stations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Translations.tr("radio_error_unavailable", language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stations) { station ->
                    val isCurrent = currentStation?.stationuuid == station.stationuuid
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectStation(station) }
                            .testTag("radio_station_${station.stationuuid}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Initials / Logo badge
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrent) TempleSaffron else TempleMaroonDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = station.getInitials(),
                                    color = TempleGoldLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = station.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (station.tags.isNotBlank()) station.tags else "Radio Station",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (station.bitrate > 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${station.bitrate}k",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = TempleGold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Play / Pause Icon
                            IconButton(
                                onClick = {
                                    if (isCurrent) {
                                        onTogglePlay()
                                    } else {
                                        onSelectStation(station)
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrent && playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = if (isCurrent) TempleGold else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. Cricket Sub-Screen (CricketData.org cricapi with 5-minute cache)
@Composable
fun CricketSubScreen(
    language: Language,
    matches: List<com.example.data.model.LiveCricketMatch>,
    isLoading: Boolean,
    minutesAgo: Long,
    infoMessage: String?,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    // Grouping: Live matches first, then Upcoming, then Recent
    val liveMatches = remember(matches) { matches.filter { it.isLive } }
    val upcomingMatches = remember(matches) { matches.filter { it.isUpcoming } }
    val recentMatches = remember(matches) { matches.filter { it.isRecent || (!it.isLive && !it.isUpcoming) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cricket_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("cricket_title", language), onBack = onBack)

        // Status & Refresh row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (minutesAgo == 0L) {
                    Translations.tr("last_updated_at", language) + " " + (if (language == Language.TELUGU) "ఇప్పుడే" else "Just now")
                } else {
                    "${Translations.tr("last_updated_at", language)} $minutesAgo ${Translations.tr("cricket_updated_mins", language)}"
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRefresh() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("cricket_refresh_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = TempleSaffron,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TempleSaffron,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = Translations.tr("refresh_btn", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TempleSaffron
                )
            }
        }

        // Info message (e.g. if key is missing or cached fallback shown)
        if (infoMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = TempleGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.3f))
            ) {
                Text(
                    text = infoMessage,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (matches.isEmpty() && isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = TempleGold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == Language.TELUGU) "క్రికెట్ స్కోర్లను లోడ్ చేస్తోంది..." else "Fetching live match scores...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = null,
                        tint = TempleGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Translations.tr("cricket_no_live", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. LIVE MATCHES SECTION
                if (liveMatches.isNotEmpty()) {
                    item {
                        CricketSectionTitle(title = Translations.tr("cricket_live_header", language), isLive = true)
                    }
                    items(liveMatches) { match ->
                        CricketMatchCard(language = language, match = match)
                    }
                }

                // 2. UPCOMING MATCHES SECTION
                if (upcomingMatches.isNotEmpty()) {
                    item {
                        CricketSectionTitle(title = Translations.tr("cricket_upcoming_header", language), isLive = false)
                    }
                    items(upcomingMatches) { match ->
                        CricketMatchCard(language = language, match = match)
                    }
                }

                // 3. RECENT MATCHES SECTION
                if (recentMatches.isNotEmpty()) {
                    item {
                        CricketSectionTitle(title = Translations.tr("cricket_recent_header", language), isLive = false)
                    }
                    items(recentMatches) { match ->
                        CricketMatchCard(language = language, match = match)
                    }
                }
            }
        }
    }
}

@Composable
private fun CricketSectionTitle(title: String, isLive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLive) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLive) LiveRed else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CricketMatchCard(
    language: Language,
    match: com.example.data.model.LiveCricketMatch
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cricket_match_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (match.isLive) LiveRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Tournament / MatchType + Badge (LIVE / UPCOMING / RESULT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.name.ifBlank { match.tournament },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TempleSaffron,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when {
                        match.isLive -> LiveRed
                        match.isUpcoming -> TempleGoldDark
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = when {
                            match.isLive -> "LIVE"
                            match.isUpcoming -> "UPCOMING"
                            else -> "RECENT"
                        },
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Team 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.getTeam1Name(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = match.getTeam1ScoreFormatted(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Team 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.getTeam2Name(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = match.getTeam2ScoreFormatted(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Match Status / Summary Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = match.status.ifBlank { if (match.isUpcoming) "Upcoming Match" else "Live match in progress" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.isLive) LiveRed else TempleMaroon
                    )
                    if (match.venue.isNotBlank()) {
                        Text(
                            text = match.venue,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// 3. Horoscope Sub-Screen
@Composable
fun HoroscopeSubScreen(
    language: Language,
    selectedRasi: Rasi,
    onSelectRasi: (Rasi) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var activeRasi by remember { mutableStateOf(selectedRasi) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("horoscope_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("horoscope_title", language), onBack = onBack)

        // Rasi selector pills
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(RasiData.allRasis) { rasi ->
                val isSel = rasi.id == activeRasi.id
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) TempleMaroon else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isSel) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { activeRasi = rasi }
                ) {
                    Text(
                        text = "${rasi.symbol} ${if (language == Language.TELUGU) rasi.nameTe else rasi.nameEn}",
                        color = if (isSel) TempleGoldLight else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Active Rasi Details Card
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, TempleGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = activeRasi.symbol, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == Language.TELUGU) activeRasi.nameTe else activeRasi.nameEn,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${if (language == Language.TELUGU) activeRasi.rulingPlanetTe else activeRasi.rulingPlanetEn} • ${if (language == Language.TELUGU) activeRasi.elementTe else activeRasi.elementEn}",
                                    fontSize = 12.sp,
                                    color = TempleSaffron
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (language == Language.TELUGU) "ఈ రోజు రాశిఫలం" else "Today's Prediction",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TempleMaroon
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.TELUGU) activeRasi.todayPredictionTe else activeRasi.todayPredictionEn,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (language == Language.TELUGU) "వారఫలం" else "Weekly Overview",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TempleMaroon
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.TELUGU) activeRasi.weeklyPredictionTe else activeRasi.weeklyPredictionEn,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TempleGold.copy(alpha = 0.15f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = Translations.tr("lucky_num", language),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${activeRasi.luckyNumber}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TempleGold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TempleSaffron.copy(alpha = 0.15f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = Translations.tr("lucky_color", language),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (language == Language.TELUGU) activeRasi.luckyColorTe else activeRasi.luckyColorEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TempleSaffron
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onSelectRasi(activeRasi) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (language == Language.TELUGU) "ఈ రాశిని డిఫాల్ట్‌గా ఉంచు" else "Set as My Rasi",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// 4. AI Pandit Sub-Screen
@Composable
fun AiPanditSubScreen(
    language: Language,
    messages: List<AiPanditMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var inputText by remember { mutableStateOf("") }

    val quickPrompts = if (language == Language.TELUGU) {
        listOf(
            "ఈ రోజు మంచి ముహూర్తం ఏమిటి?",
            "ఏకాదశి ఉపవాస నియమాలు ఏమిటి?",
            "నా రాశి ఫలితాలు ఎలా ఉన్నాయి?",
            "కార్తీక దీపోత్సవ విశిష్టత ఏమిటి?"
        )
    } else {
        listOf(
            "What is today's best Muhurtham?",
            "What are Ekadashi fasting rules?",
            "How is my horoscope today?",
            "Significance of Karthika Deepam?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_pandit_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("ai_pandit_title", language), onBack = onBack)

        // Subtitle banner
        Surface(color = TempleMaroonDark, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = Translations.tr("ai_pandit_subtitle", language),
                color = TempleGoldLight,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.isUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TempleMaroon),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🕉", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) TempleMaroon else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isUser) TempleMaroon else TempleGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TempleGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.TELUGU) "AI పండిట్ ఆలోచిస్తున్నారు..." else "Pandit is consulting sacred texts...",
                            fontSize = 12.sp,
                            color = TempleSaffron
                        )
                    }
                }
            }
        }

        // Quick prompt chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TempleGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = Translations.tr("ai_pandit_placeholder", language),
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_pandit_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TempleGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(TempleMaroon)
                    .testTag("ai_pandit_send_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = TempleGoldLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// 5. Festivals Sub-Screen (Grouped by month with Next Festival Countdown)
@Composable
fun FestivalsSubScreen(
    language: Language,
    selectedCity: City,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    var showVrats by remember { mutableStateOf(false) }

    // This year and next, calculated for the user's city.
    val festivals = remember(selectedCity, today) {
        listOf(today.year, today.year + 1).flatMap {
            FestivalCalendar.forYear(context, it, selectedCity.latitude, selectedCity.longitude)
        }
    }
    val nextFestival = remember(festivals) {
        festivals.firstOrNull { it.isMajor && !it.date.isBefore(today) }
    }

    // Group festivals by month
    val groupedFestivals = remember(festivals, showVrats, language) {
        festivals.filter { showVrats || it.isMajor }.groupBy {
            if (language == Language.TELUGU) it.monthGroupTe else it.monthGroupEn
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("festivals_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("festivals_title", language), onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Countdown Card for Next Festival
            if (nextFestival != null) item {
                val daysLeft = nextFestival.getDaysLeft(today)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("next_festival_countdown_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleMaroonDark),
                    border = BorderStroke(1.5.dp, TempleGold)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.TELUGU) "రాబోయే ప్రధాన పర్వదినం" else "Upcoming Major Festival",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TempleGoldLight
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TempleSaffron
                            ) {
                                val countdownText = when {
                                    daysLeft <= 0 -> Translations.tr("countdown_today", language)
                                    daysLeft == 1L -> Translations.tr("countdown_tomorrow", language)
                                    else -> "$daysLeft ${Translations.tr("countdown_days", language)}"
                                }
                                Text(
                                    text = countdownText,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (language == Language.TELUGU) nextFestival.nameTe else nextFestival.nameEn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TempleGoldLight
                        )

                        Text(
                            text = "${nextFestival.dateString} • ${if (language == Language.TELUGU) nextFestival.tithiTe else nextFestival.tithiEn}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == Language.TELUGU) nextFestival.descriptionTe else nextFestival.descriptionEn,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Translations.tr("festival_show_vrats", language),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = showVrats,
                        onCheckedChange = { showVrats = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleMaroon),
                        modifier = Modifier.testTag("festival_show_vrats")
                    )
                }
            }

            // Month grouped list
            groupedFestivals.forEach { (monthLabel, festivalsInMonth) ->
                item {
                    Text(
                        text = monthLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TempleMaroon,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }

                items(festivalsInMonth) { festival ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("festival_card_${festival.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == Language.TELUGU) festival.nameTe else festival.nameEn,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TempleGold.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = festival.dateString,
                                        color = TempleGoldDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (language == Language.TELUGU) festival.tithiTe else festival.tithiEn,
                                fontSize = 12.sp,
                                color = TempleSaffron,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (festival.isVerified) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AuspiciousGreen.copy(alpha = 0.15f),
                                    modifier = Modifier.testTag("festival_verified_${festival.id}")
                                ) {
                                    Text(
                                        text = "✓ ${Translations.tr("festival_verified", language)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuspiciousGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            val note = if (language == Language.TELUGU) festival.noteTe else festival.noteEn
                            if (note.isNotBlank()) {
                                Text(
                                    text = note,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (language == Language.TELUGU) festival.descriptionTe else festival.descriptionEn,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (festival.ritualsTe.isNotBlank() || festival.ritualsEn.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "విశిష్ట పూజ: ${if (language == Language.TELUGU) festival.ritualsTe else festival.ritualsEn}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** One step of the "add widget" guide: a small phone drawing beside the step's text. */
@Composable
private fun WidgetHowToStep(
    number: Int,
    title: String,
    description: String,
    illustration: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 84.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, TempleGold.copy(alpha = 0.7f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$number. $title",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 6. Settings Sub-Screen
@Composable
fun SettingsSubScreen(
    language: Language,
    selectedCity: City,
    selectedRasi: Rasi,
    themeMode: ThemeMode,
    notificationsEnabled: Boolean,
    morningNotifEnabled: Boolean = true,
    morningNotifTime: String = "06:30",
    rahuReminderEnabled: Boolean = true,
    festivalReminderEnabled: Boolean = true,
    onLanguageChange: (Language) -> Unit,
    onCityChange: (City) -> Unit,
    onRasiChange: (Rasi) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onMorningNotifToggle: (Boolean) -> Unit = {},
    onMorningTimeChange: (String) -> Unit = {},
    onRahuReminderToggle: (Boolean) -> Unit = {},
    onFestivalReminderToggle: (Boolean) -> Unit = {},
    onTestNotification: (String) -> Unit = {},
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var showCityDialog by remember { mutableStateOf(false) }
    var showRasiDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showWidgetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("settings_title", language), onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = TempleMaroon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.tr("setting_language", language),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (language == Language.TELUGU) TempleMaroon else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onLanguageChange(Language.TELUGU) }
                                    .testTag("setting_lang_te")
                            ) {
                                Text(
                                    text = "తెలుగు (Default)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (language == Language.TELUGU) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (language == Language.ENGLISH) TempleMaroon else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onLanguageChange(Language.ENGLISH) }
                                    .testTag("setting_lang_en")
                            ) {
                                Text(
                                    text = "English",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (language == Language.ENGLISH) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Theme Mode Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = TempleMaroon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.tr("setting_theme", language),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            Triple(ThemeMode.SYSTEM, Translations.tr("theme_system", language), "setting_theme_system"),
                            Triple(ThemeMode.LIGHT, Translations.tr("theme_light", language), "setting_theme_light"),
                            Triple(ThemeMode.DARK, Translations.tr("theme_dark", language), "setting_theme_dark")
                        ).forEach { (mode, label, tag) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onThemeModeChange(mode) }
                                    .padding(vertical = 4.dp)
                                    .testTag(tag),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = { onThemeModeChange(mode) },
                                    colors = RadioButtonDefaults.colors(selectedColor = TempleGold)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Location / City Setting
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showCityDialog = true }
                        .testTag("setting_city_row"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TempleSaffron)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Translations.tr("setting_city", language),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == Language.TELUGU) selectedCity.nameTe else selectedCity.nameEn,
                                    fontSize = 12.sp,
                                    color = TempleGold
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Zodiac Sign Setting
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showRasiDialog = true }
                        .testTag("setting_rasi_row"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedRasi.symbol, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Translations.tr("setting_rasi", language),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == Language.TELUGU) selectedRasi.nameTe else selectedRasi.nameEn,
                                    fontSize = 12.sp,
                                    color = TempleGold
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Notifications Card (Morning Panchang, Rahu Kalam, Festival)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = TempleMaroon)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = Translations.tr("setting_notifications", language),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = onNotificationsToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TempleGold,
                                    checkedTrackColor = TempleMaroon
                                ),
                                modifier = Modifier.testTag("setting_notifications_switch")
                            )
                        }

                        if (notificationsEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // 1. Morning Panchang Notification
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Translations.tr("setting_morning_notif", language),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = Translations.tr("setting_morning_notif_desc", language),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = morningNotifEnabled,
                                    onCheckedChange = onMorningNotifToggle,
                                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleMaroon)
                                )
                            }

                            if (morningNotifEnabled) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${Translations.tr("setting_morning_time", language)}: $morningNotifTime AM",
                                        fontSize = 12.sp,
                                        color = TempleSaffron,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == Language.TELUGU) "మార్చు" else "Change",
                                        fontSize = 11.sp,
                                        color = TempleGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. Rahu Kalam 15-minute Reminder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Translations.tr("setting_rahu_notif", language),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = Translations.tr("setting_rahu_notif_desc", language),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = rahuReminderEnabled,
                                    onCheckedChange = onRahuReminderToggle,
                                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleMaroon)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3. Festival Eve Reminder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Translations.tr("setting_festival_notif", language),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = Translations.tr("setting_festival_notif_desc", language),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = festivalReminderEnabled,
                                    onCheckedChange = onFestivalReminderToggle,
                                    colors = SwitchDefaults.colors(checkedThumbColor = TempleGold, checkedTrackColor = TempleMaroon)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Test Notification buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TempleGold.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onTestNotification("MORNING") }
                                ) {
                                    Text(
                                        text = "Test Morning",
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TempleSaffron.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, TempleSaffron.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onTestNotification("RAHU") }
                                ) {
                                    Text(
                                        text = "Test Rahu",
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TempleMaroon.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, TempleMaroon.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onTestNotification("FESTIVAL") }
                                ) {
                                    Text(
                                        text = "Test Festival",
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add Home-Screen Widget Instructions
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showWidgetDialog = true }
                        .testTag("setting_add_widget_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = TempleSaffron)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = Translations.tr("setting_add_widget", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Privacy Policy Link
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showPrivacyDialog = true }
                        .testTag("setting_privacy_policy_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = TempleMaroon)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = Translations.tr("setting_privacy", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // About Shubhodaya & Disclaimers Link
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showAboutDialog = true }
                        .testTag("setting_about_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TempleGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = Translations.tr("setting_about", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            // App Version Info
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "శుభోదయ • Shubhodaya",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TempleGold
                    )
                    Text(
                        text = Translations.tr("app_version", language),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text(Translations.tr("onboard_step2_title", language), fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(CityData.cities) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCityChange(city)
                                    showCityDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (language == Language.TELUGU) city.nameTe else city.nameEn,
                                fontSize = 14.sp,
                                fontWeight = if (city.id == selectedCity.id) FontWeight.Bold else FontWeight.Normal
                            )
                            if (city.id == selectedCity.id) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TempleGold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text(Translations.tr("close", language), color = TempleGold)
                }
            }
        )
    }

    // Rasi Selection Dialog
    if (showRasiDialog) {
        AlertDialog(
            onDismissRequest = { showRasiDialog = false },
            title = { Text(Translations.tr("onboard_step3_title", language), fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(RasiData.allRasis) { rasi ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRasiChange(rasi)
                                    showRasiDialog = false
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = rasi.symbol, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == Language.TELUGU) rasi.nameTe else rasi.nameEn,
                                    fontSize = 14.sp,
                                    fontWeight = if (rasi.id == selectedRasi.id) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (rasi.id == selectedRasi.id) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TempleGold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRasiDialog = false }) {
                    Text(Translations.tr("close", language), color = TempleGold)
                }
            }
        )
    }

    // Add Widget Steps Dialog
    if (showWidgetDialog) {
        AlertDialog(
            onDismissRequest = { showWidgetDialog = false },
            title = {
                Text(
                    text = Translations.tr("setting_add_widget", language),
                    fontWeight = FontWeight.Bold,
                    color = TempleGold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 380.dp)
                        .testTag("add_widget_steps"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        WidgetHowToStep(
                            number = 1,
                            title = Translations.tr("widget_step1_title", language),
                            description = Translations.tr("widget_step1_desc", language)
                        ) {
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = TempleSaffron,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    item {
                        WidgetHowToStep(
                            number = 2,
                            title = Translations.tr("widget_step2_title", language),
                            description = Translations.tr("widget_step2_desc", language)
                        ) {
                            Icon(
                                Icons.Default.Widgets,
                                contentDescription = null,
                                tint = TempleGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    item {
                        WidgetHowToStep(
                            number = 3,
                            title = Translations.tr("widget_step3_title", language),
                            description = Translations.tr("widget_step3_desc", language)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.shubhodaya_icon),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWidgetDialog = false }) {
                    Text(Translations.tr("close", language), color = TempleGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = Translations.tr("privacy_policy_title", language),
                    fontWeight = FontWeight.Bold,
                    color = TempleGold
                )
            },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                    item {
                        Text(
                            text = Translations.tr("privacy_policy_body", language),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(Translations.tr("close", language), color = TempleGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // About Shubhodaya Dialog with Mandatory Disclaimer
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TempleGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.tr("about_title", language),
                        fontWeight = FontWeight.Bold,
                        color = TempleGold
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = Translations.tr("about_desc", language),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    item {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = TempleMaroon.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (language == Language.TELUGU) "ముఖ్య ప్రకటన (Disclaimer):" else "Disclaimer:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TempleGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = Translations.tr("about_disclaimer", language),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = Translations.tr("about_sources", language),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(Translations.tr("close", language), color = TempleGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Morning Notification Time Picker Dialog
    if (showTimePicker) {
        val times = listOf("05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00")
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    text = Translations.tr("setting_morning_time", language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    times.forEach { t ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMorningTimeChange(t)
                                    showTimePicker = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$t AM",
                                fontSize = 15.sp,
                                fontWeight = if (morningNotifTime == t) FontWeight.Bold else FontWeight.Normal,
                                color = if (morningNotifTime == t) TempleSaffron else MaterialTheme.colorScheme.onSurface
                            )
                            if (morningNotifTime == t) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TempleGold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(Translations.tr("close", language), color = TempleGold)
                }
            }
        )
    }
}

// 7. Premium Sub-Screen
@Composable
fun PremiumSubScreen(
    language: Language,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("premium_subscreen")
    ) {
        SubScreenHeader(title = Translations.tr("premium_title", language), onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = TempleMaroonDark),
                    border = BorderStroke(2.dp, TempleGold)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "శుభోదయ గోల్డ్ (Shubhodaya Gold)",
                            color = TempleGoldLight,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Translations.tr("premium_subtitle", language),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val features = listOf(
                Pair("100% Ad-Free Experience", "వీడియో ప్లేయర్ మరియు యాప్‌లో సంపూర్ణ ప్రకటన రహిత ఆధ్యాత్మిక ప్రశాంతత"),
                Pair("Kundali & Matching", "సంపూర్ణ జాతక చక్రం మరియు వివాహ గుణమేళనం నివేదికలు"),
                Pair("Personalized Muhurtham", "గృహప్రవేశం, వివాహం మరియు ఉపనయన ముహూర్తాల ప్రత్యేకాన్వేషణ"),
                Pair("Offline Panchang Downloads", "నెట్ లేకపోయినా పనిచేసే వార్షిక పంచాంగ డౌన్‌లోడ్")
            )

            items(features) { (titleEn, descTe) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TempleGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = titleEn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = descTe,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { /* simulated gold subscription */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleGold, contentColor = TempleMaroonDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == Language.TELUGU) "ఉచితంగా ప్రారంభించండి (7 రోజుల ట్రయల్)" else "Start 7-Day Free Trial",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
