package com.example.services

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import com.example.data.model.RadioBrowserStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RadioPlayerState(
    val currentStation: RadioBrowserStation? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RadioPlayerManager(context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val appContext = context.applicationContext

    private val _playerState = MutableStateFlow(RadioPlayerState())
    val playerState: StateFlow<RadioPlayerState> = _playerState.asStateFlow()

    fun playStation(station: RadioBrowserStation) {
        // If already playing this station, toggle
        if (_playerState.value.currentStation?.stationuuid == station.stationuuid && _playerState.value.isPlaying) {
            pause()
            return
        }

        stop()

        _playerState.value = RadioPlayerState(
            currentStation = station,
            isPlaying = false,
            isLoading = true,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setWakeMode(appContext, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                val streamUrl = station.getStreamUrl()
                setDataSource(streamUrl)

                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        errorMessage = null
                    )
                }

                setOnErrorListener { _, what, extra ->
                    releasePlayer()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "Station unavailable, try another"
                    )
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            releasePlayer()
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isLoading = false,
                errorMessage = "Station unavailable, try another"
            )
        }
    }

    fun resume() {
        mediaPlayer?.let { mp ->
            try {
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true, isLoading = false)
            } catch (e: Exception) {
                _playerState.value.currentStation?.let { playStation(it) }
            }
        } ?: run {
            _playerState.value.currentStation?.let { playStation(it) }
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _playerState.value = _playerState.value.copy(isPlaying = false, isLoading = false)
        } catch (e: Exception) {
            stop()
        }
    }

    fun togglePlay() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun stop() {
        releasePlayer()
        _playerState.value = RadioPlayerState(
            currentStation = null,
            isPlaying = false,
            isLoading = false,
            errorMessage = null
        )
    }

    fun clearError() {
        _playerState.value = _playerState.value.copy(errorMessage = null)
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
        } catch (e: Exception) {
            // ignore
        }
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
    }
}
