package com.example.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.BuildConfig
import com.example.data.model.Channel
import com.example.data.model.ChannelCategory
import com.example.data.model.ChannelsData
import com.example.data.model.Language
import com.example.data.repository.LiveStatusRepository
import com.example.i18n.Translations
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleGoldLight
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleSaffron
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.net.URLEncoder

@Composable
fun LiveTvScreen(
    language: Language,
    selectedChannel: Channel,
    selectedCategory: String,
    favoriteChannelIds: Set<String>,
    onSelectCategory: (String) -> Unit,
    onSelectChannel: (Channel) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    liveTvViewModel: LiveTvViewModel = viewModel()
) {
    val allChannels = ChannelsData.allChannels
    val liveStatus by liveTvViewModel.state.collectAsStateWithLifecycle()

    // Checks the shown channel when the screen opens and whenever another channel is picked.
    LaunchedEffect(selectedChannel.id) { liveTvViewModel.onChannelShown(selectedChannel) }

    // Filter channels based on category chip
    val displayedChannels = remember(selectedCategory, favoriteChannelIds, allChannels) {
        when (selectedCategory) {
            "FAVORITES" -> allChannels.filter { favoriteChannelIds.contains(it.id) }
            "ALL" -> {
                // Show favorites first when on "All"
                val favs = allChannels.filter { favoriteChannelIds.contains(it.id) }
                val nonFavs = allChannels.filter { !favoriteChannelIds.contains(it.id) }
                favs + nonFavs
            }
            "Devotional" -> allChannels.filter { it.category == "Devotional" }
            "Telugu News" -> allChannels.filter { it.category == "Telugu News" }
            "National News" -> allChannels.filter { it.category == "National News" }
            "Entertainment" -> allChannels.filter { it.category == "Entertainment" }
            else -> allChannels
        }
    }

    // Channels in the same category for the "More in this category" row
    val moreInSameCategory = remember(selectedChannel, allChannels) {
        allChannels.filter { it.category == selectedChannel.category && it.id != selectedChannel.id }
    }

    val categoryChips = listOf(
        Pair("FAVORITES", Translations.tr("cat_favorites", language)),
        Pair("ALL", Translations.tr("cat_all", language)),
        Pair("Devotional", Translations.tr("cat_devotional", language)),
        Pair("Telugu News", Translations.tr("cat_telugu_news", language)),
        Pair("National News", Translations.tr("cat_national_news", language)),
        Pair("Entertainment", Translations.tr("cat_entertainment", language))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("livetv_screen"),
        contentPadding = PaddingValues(bottom = 80.dp) // padding for persistent player bar
    ) {
        // Zero Ads Trust Guarantee Banner
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TempleMaroonDark
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = TempleGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Translations.tr("no_ads_promise", language),
                        color = TempleGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Full-width 16:9 YouTube Player / Friendly fallback
        item {
            FullWidthLivePlayer(
                language = language,
                channel = selectedChannel,
                liveStatus = liveStatus,
                isFavorite = favoriteChannelIds.contains(selectedChannel.id),
                onToggleFavorite = { onToggleFavorite(selectedChannel.id) },
                onRetry = { liveTvViewModel.onChannelShown(selectedChannel, force = true) }
            )
        }

        // "More in this category" Row
        if (moreInSameCategory.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "${Translations.tr("more_in_category", language)} (${if (language == Language.TELUGU) selectedChannel.category else selectedChannel.category})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(moreInSameCategory) { moreChan ->
                            MoreChannelPillCard(
                                channel = moreChan,
                                availability = availabilityOf(moreChan, liveStatus),
                                language = language,
                                onClick = { onSelectChannel(moreChan) }
                            )
                        }
                    }
                }
            }
        }

        // Category Chips at Top of Channel Grid
        item {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryChips) { (key, label) ->
                    val isSelected = selectedCategory == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(key) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleMaroon,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("tv_category_chip_$key")
                    )
                }
            }
        }

        // Grid of Channel Cards
        if (displayedChannels.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedCategory == "FAVORITES") Translations.tr("no_favorites_yet", language)
                        else "No channels available in this category.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // 2-column channel grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val chunked = displayedChannels.chunked(2)
                    chunked.forEach { rowChannels ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowChannels.forEach { ch ->
                                ChannelGridCard(
                                    channel = ch,
                                    availability = availabilityOf(ch, liveStatus),
                                    language = language,
                                    isSelected = ch.id == selectedChannel.id,
                                    isFavorite = favoriteChannelIds.contains(ch.id),
                                    onSelect = { onSelectChannel(ch) },
                                    onToggleFavorite = { onToggleFavorite(ch.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowChannels.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullWidthLivePlayer(
    language: Language,
    channel: Channel,
    liveStatus: LiveTvUiState,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit
) {
    val success = liveStatus as? LiveTvUiState.Success
    val liveNow = success?.statuses?.get(channel.youtubeChannelId)?.takeIf { it.isLive }
    // Only a well-formed ID ever reaches the player.
    val videoId = liveNow?.videoId?.takeIf(LiveStatusRepository.VIDEO_ID::matches)
    var playerError by remember(videoId) { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("full_width_live_player"),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column {
            // 16:9 Player Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF0F0B0C)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    liveStatus is LiveTvUiState.Loading ||
                        (videoId == null && success?.checking?.contains(channel.youtubeChannelId) == true) ->
                        CircularProgressIndicator(color = TempleGold, modifier = Modifier.size(36.dp))

                    liveStatus is LiveTvUiState.Error -> PlayerMessage(
                        language = language,
                        title = Translations.tr("live_offline", language),
                        description = Translations.tr("live_status_unavailable", language),
                        watchUrl = YouTubeLinks.channel(channel),
                        onRetry = onRetry,
                        testTag = "live_offline_state"
                    )

                    videoId != null && playerError == null -> key(videoId) {
                        YouTubeLivePlayer(
                            videoId = videoId,
                            onPlayerError = { playerError = it },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    videoId != null -> PlayerMessage(
                        language = language,
                        title = Translations.tr("live_player_error", language),
                        description = null,
                        watchUrl = YouTubeLinks.watch(videoId),
                        testTag = "live_player_error_state"
                    )

                    else -> PlayerMessage(
                        language = language,
                        title = Translations.tr("not_live_now", language),
                        description = Translations.tr(
                            if (success?.liveCheckAvailable == false) "live_check_off_desc" else "not_live_now_desc",
                            language
                        ),
                        watchUrl = YouTubeLinks.channel(channel),
                        testTag = "live_not_live_state"
                    )
                }

                // Top LIVE tag overlay
                if (videoId != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        AvailabilityTag(ChannelAvailability.LIVE, language)
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                Text(
                    text = "channel=${channel.youtubeChannelId.ifBlank { "none" }} video=${videoId ?: "none"} error=${playerError ?: "none"}",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .testTag("live_player_debug")
                )
            }

            // Player Bar Title & Favorite Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TempleMaroonDark)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == Language.TELUGU) channel.nameTelugu else channel.name,
                        color = TempleGoldLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = liveNow?.title ?: channel.category,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("channel_favorite_btn_${channel.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) LiveRed else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelGridCard(
    channel: Channel,
    availability: ChannelAvailability,
    language: Language,
    isSelected: Boolean,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .alpha(if (availability == ChannelAvailability.OFFLINE) OFFLINE_ALPHA else 1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("channel_card_${channel.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Initials badge or logo
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when (channel.category) {
                                "Devotional" -> TempleMaroon
                                "Telugu News" -> Color(0xFF1E3A8A)
                                "National News" -> Color(0xFF15803D)
                                else -> Color(0xFF7C2D12)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = channel.getInitials(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) LiveRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == Language.TELUGU) channel.nameTelugu else channel.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.category,
                    fontSize = 10.sp,
                    color = TempleSaffron,
                    fontWeight = FontWeight.SemiBold
                )

                AvailabilityTag(availability, language)

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TempleGold
                    ) {
                        Text(
                            text = "PLAYING",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TempleMaroonDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreChannelPillCard(
    channel: Channel,
    availability: ChannelAvailability,
    language: Language,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .alpha(if (availability == ChannelAvailability.OFFLINE) OFFLINE_ALPHA else 1f)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(TempleMaroon.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.getInitials(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (language == Language.TELUGU) channel.nameTelugu else channel.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))
            AvailabilityTag(availability, language)
        }
    }
}

private const val OFFLINE_ALPHA = 0.55f

private enum class ChannelAvailability { LIVE, OFFLINE, UNKNOWN }

private fun availabilityOf(channel: Channel, state: LiveTvUiState): ChannelAvailability {
    val status = (state as? LiveTvUiState.Success)?.statuses?.get(channel.youtubeChannelId) ?: return ChannelAvailability.UNKNOWN
    return if (status.isLive) ChannelAvailability.LIVE else ChannelAvailability.OFFLINE
}

@Composable
private fun AvailabilityTag(availability: ChannelAvailability, language: Language) {
    val (label, color, dotColor) = when (availability) {
        ChannelAvailability.LIVE -> Triple("LIVE", LiveRed, Color(0xFF4CAF50))
        ChannelAvailability.OFFLINE -> Triple(Translations.tr("live_offline", language), Color(0xFF757575), Color(0xFF9E9E9E))
        ChannelAvailability.UNKNOWN -> return
    }
    Surface(shape = RoundedCornerShape(4.dp), color = color) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Links used when a stream can't play in the app. */
internal object YouTubeLinks {
    fun watch(videoId: String) = "https://www.youtube.com/watch?v=$videoId"

    /** The channel's live page, or a YouTube search when we don't have its channel ID. */
    fun channel(channel: Channel): String =
        if (LiveStatusRepository.CHANNEL_ID.matches(channel.youtubeChannelId)) {
            "https://www.youtube.com/channel/${channel.youtubeChannelId}/live"
        } else {
            "https://www.youtube.com/results?search_query=" + URLEncoder.encode("${channel.name} live", "UTF-8")
        }
}

private fun openYouTube(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        Log.w(PLAYER_TAG, "No app could open $url", e)
        Toast.makeText(context, url, Toast.LENGTH_LONG).show()
    }
}

private const val PLAYER_TAG = "LiveTvPlayer"

/** Holds the view so onDispose can release it; deliberately not Compose state. */
private class PlayerHolder {
    var view: YouTubePlayerView? = null
}

/**
 * android-youtube-player inside an AndroidView. The video loads only after onReady, the view follows the
 * screen's lifecycle, and it is released when it leaves composition. Any player error, including the
 * embedding-disabled codes 101/150/152/153, is reported so the screen can offer "Watch on YouTube".
 */
@Composable
private fun YouTubeLivePlayer(
    videoId: String,
    onPlayerError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val reportError by rememberUpdatedState(onPlayerError)
    val holder = remember { PlayerHolder() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            YouTubePlayerView(context).also { view ->
                view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                view.enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(view)
                holder.view = view

                val options = IFramePlayerOptions.Builder(context)
                    .controls(1)
                    .autoplay(1)
                    .rel(0)
                    .ivLoadPolicy(3)
                    .fullscreen(0)
                    .build()
                view.initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        Log.w(PLAYER_TAG, "Player error for $videoId: $error")
                        reportError(error.name)
                    }
                }, options)
                view.guardAgainstRendererCrash { reportError("RENDERER_GONE") }
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        onDispose {
            holder.view?.let { view ->
                lifecycleOwner.lifecycle.removeObserver(view)
                view.release()
            }
            holder.view = null
        }
    }
}

/**
 * If a WebView's renderer process dies and no WebViewClient handles it, Android kills the whole app with
 * no Java exception in Logcat. The player library sets no WebViewClient, so we add one that survives it.
 */
private fun ViewGroup.guardAgainstRendererCrash(onGone: () -> Unit) {
    val webView = findWebView(this) ?: return
    webView.webViewClient = object : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O) // Only called on API 26+, where renderers run in their own process.
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            Log.e(PLAYER_TAG, "Player renderer gone (crashed=${detail.didCrash()}); showing Watch on YouTube instead")
            (view.parent as? ViewGroup)?.removeView(view)
            onGone()
            return true
        }

        // Links inside the player (YouTube logo, title) open in the YouTube app, as they did with no client set.
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (!request.isForMainFrame) return false
            openYouTube(view.context, request.url.toString())
            return true
        }
    }
}

private fun findWebView(view: View): WebView? = when (view) {
    is WebView -> view
    is ViewGroup -> (0 until view.childCount).firstNotNullOfOrNull { findWebView(view.getChildAt(it)) }
    else -> null
}

/** Message + "Watch on YouTube" (and optional Retry) shown in place of the player. */
@Composable
private fun PlayerMessage(
    language: Language,
    title: String,
    description: String?,
    watchUrl: String,
    testTag: String,
    onRetry: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B0C))
            .padding(16.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.TvOff,
            contentDescription = null,
            tint = TempleGoldLight,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        description?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onRetry != null) {
                OutlinedButton(
                    onClick = onRetry,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("live_retry_button")
                ) {
                    Text(Translations.tr("live_retry", language), color = Color.White)
                }
            }
            Button(
                onClick = { openYouTube(context, watchUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = LiveRed, contentColor = Color.White),
                modifier = Modifier.testTag("watch_on_youtube_button")
            ) {
                Text(Translations.tr("live_watch_on_youtube", language), fontWeight = FontWeight.Bold)
            }
        }
    }
}
