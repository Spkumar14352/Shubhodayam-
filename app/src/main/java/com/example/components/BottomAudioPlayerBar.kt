package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.i18n.Translations
import com.example.services.RadioPlayerState
import com.example.ui.theme.muhurthamColors

@Composable
fun BottomAudioPlayerBar(
    language: Language,
    playerState: RadioPlayerState,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onClearError: () -> Unit,
    onOpenRadioScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val station = playerState.currentStation
    val isVisible = station != null || playerState.errorMessage != null

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .testTag("bottom_audio_player_bar")
        ) {
            // Error notice banner if stream fails
            playerState.errorMessage?.let { errorMsg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.muhurthamColors.inauspiciousRedBg),
                    border = BorderStroke(1.dp, MaterialTheme.muhurthamColors.inauspiciousRed)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.muhurthamColors.inauspiciousRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Translations.tr("station_unavailable", language),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.muhurthamColors.inauspiciousRed,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Error",
                                tint = MaterialTheme.muhurthamColors.inauspiciousRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Fixed Single Audio Player Bar
            if (station != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenRadioScreen() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Station Favicon / Badge & Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = station.getInitials(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = station.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (playerState.isPlaying) Color(0xFF1B5E20).copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (playerState.isPlaying) "LIVE RADIO" else "RADIO",
                                            color = if (playerState.isPlaying) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (playerState.isPlaying) {
                                        MiniEqualizerBars()
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = if (station.teluguName.isNotBlank()) station.teluguName else "${station.bitrate} kbps",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Player Controls: Play/Pause/Loading & Stop
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (playerState.isLoading) {
                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = onTogglePlay,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .testTag("fixed_player_play_pause_btn")
                                ) {
                                    Icon(
                                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Stop button
                            IconButton(
                                onClick = onStop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("fixed_player_stop_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Stop Radio",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniEqualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_eq")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 3f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(animation = tween(350), repeatMode = RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(animation = tween(280), repeatMode = RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 11f,
        animationSpec = infiniteRepeatable(animation = tween(420), repeatMode = RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        modifier = Modifier.height(12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(1.5.dp)
    ) {
        Box(modifier = Modifier.width(2.dp).height(h1.dp).background(Color(0xFF2E7D32), RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h2.dp).background(Color(0xFF4CAF50), RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h3.dp).background(Color(0xFF2E7D32), RoundedCornerShape(1.dp)))
    }
}
