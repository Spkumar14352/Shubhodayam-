package com.example.services

import android.content.Context
import com.example.data.model.RadioBrowserStation
import kotlinx.coroutines.flow.StateFlow

data class RadioPlayerState(
    val currentStation: RadioBrowserStation? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RadioPlayerManager(context: Context) {
    private val appContext = context.applicationContext

    val playerState: StateFlow<RadioPlayerState> = RadioPlaybackService.servicePlayerState

    fun playStation(station: RadioBrowserStation) {
        RadioPlaybackService.play(appContext, station)
    }

    fun resume() {
        RadioPlaybackService.resume(appContext)
    }

    fun pause() {
        RadioPlaybackService.pause(appContext)
    }

    fun togglePlay() {
        RadioPlaybackService.togglePlay(appContext)
    }

    fun stop() {
        RadioPlaybackService.stop(appContext)
    }

    fun clearError() {
        RadioPlaybackService.clearError()
    }
}
