package com.example.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Channel
import com.example.data.model.ChannelsData
import com.example.data.repository.LiveSource
import com.example.data.repository.LiveStatus
import com.example.data.repository.LiveStatusRepository
import com.example.data.repository.LiveStatusSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LiveTvUiState {
    data object Loading : LiveTvUiState

    /**
     * [statuses] holds every channel checked so far; a channel missing from it is unknown.
     * [liveCheckAvailable] is false when no server URL or API key is set, so nothing is ever checked.
     */
    data class Success(
        val statuses: Map<String, LiveStatus>,
        val liveCheckAvailable: Boolean,
        val checking: Set<String> = emptySet()
    ) : LiveTvUiState

    /** The live check failed (offline, bad response, API error). Channels still show and can be opened on YouTube. */
    data object Error : LiveTvUiState
}

class LiveTvViewModel(
    private val repository: LiveStatusSource = LiveStatusRepository(),
    private val now: () -> Long = System::currentTimeMillis
) : ViewModel() {
    private val _state = MutableStateFlow<LiveTvUiState>(
        if (repository.source == LiveSource.NONE) LiveTvUiState.Success(emptyMap(), liveCheckAvailable = false) else LiveTvUiState.Loading
    )
    val state: StateFlow<LiveTvUiState> = _state.asStateFlow()

    private val known = mutableMapOf<String, LiveStatus>()
    private val checkedAt = mutableMapOf<String, Long>()
    private var serverCheckedAt = 0L
    private var serverLoading = false

    /** Called whenever a channel is shown in the player; checks it if its status is missing or stale. */
    fun onChannelShown(channel: Channel, force: Boolean = false) {
        when (repository.source) {
            LiveSource.NONE -> Unit
            LiveSource.SERVER -> if (force || _state.value !is LiveTvUiState.Success || now() - serverCheckedAt > MAX_AGE_MS) loadAll()
            LiveSource.YOUTUBE_API -> check(channel.youtubeChannelId, force)
        }
    }

    private fun loadAll() {
        if (serverLoading) return
        serverLoading = true
        _state.value = LiveTvUiState.Loading
        viewModelScope.launch {
            val result = safely { repository.fetchAll() }
            serverLoading = false
            _state.value = if (result == null) {
                LiveTvUiState.Error
            } else {
                serverCheckedAt = now()
                // Channels the server didn't mention are not live.
                val offline = ChannelsData.allChannels.map { it.youtubeChannelId }.filter { it.isNotBlank() }
                    .associateWith { LiveStatus(it, isLive = false, videoId = null, title = null, thumbnail = null) }
                LiveTvUiState.Success(offline + result, liveCheckAvailable = true)
            }
        }
    }

    private fun check(channelId: String, force: Boolean) {
        val current = _state.value as? LiveTvUiState.Success ?: LiveTvUiState.Success(known.toMap(), liveCheckAvailable = true)
        // A channel with no ID can't be checked; the screen offers a YouTube search for it instead.
        if (!LiveStatusRepository.CHANNEL_ID.matches(channelId)) {
            _state.value = current
            return
        }
        if (channelId in current.checking) return
        val fresh = channelId in known && now() - (checkedAt[channelId] ?: 0L) <= MAX_AGE_MS
        if (fresh && !force) {
            _state.value = current
            return
        }

        _state.value = current.copy(checking = current.checking + channelId)
        viewModelScope.launch {
            val status = safely { repository.fetchChannel(channelId) }
            if (status != null) {
                known[channelId] = status
                checkedAt[channelId] = now()
            }
            _state.update { state ->
                val checking = (state as? LiveTvUiState.Success)?.checking.orEmpty() - channelId
                if (status == null) LiveTvUiState.Error else LiveTvUiState.Success(known.toMap(), liveCheckAvailable = true, checking = checking)
            }
        }
    }

    /** The repository already catches its own errors; this is a last line of defence so the screen never crashes. */
    private suspend fun <T> safely(block: suspend () -> T?): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(TAG, "Live status check failed", e)
        null
    }

    companion object {
        private const val TAG = "LiveTvViewModel"
        const val MAX_AGE_MS = 5 * 60 * 1000L
    }
}
