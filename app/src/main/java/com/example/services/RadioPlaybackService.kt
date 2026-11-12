package com.example.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.MainActivity
import com.example.R
import com.example.data.model.RadioBrowserStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioPlaybackService : MediaSessionService() {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    companion object {
        private const val TAG = "RadioPlaybackService"
        const val CHANNEL_ID = "radio_playback_channel"
        const val NOTIFICATION_ID = 2001

        const val ACTION_PLAY_STATION = "com.example.services.ACTION_PLAY_STATION"
        const val ACTION_TOGGLE_PLAY = "com.example.services.ACTION_TOGGLE_PLAY"
        const val ACTION_PAUSE = "com.example.services.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.services.ACTION_RESUME"
        const val ACTION_STOP = "com.example.services.ACTION_STOP"

        const val EXTRA_STATION_UUID = "extra_station_uuid"
        const val EXTRA_STATION_NAME = "extra_station_name"
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_TELUGU_NAME = "extra_telugu_name"
        const val EXTRA_TAGS = "extra_tags"
        const val EXTRA_BITRATE = "extra_bitrate"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_FREQUENCY = "extra_frequency"

        private val _servicePlayerState = MutableStateFlow(RadioPlayerState())
        val servicePlayerState: StateFlow<RadioPlayerState> = _servicePlayerState.asStateFlow()

        var activeInstance: RadioPlaybackService? = null
            private set

        fun play(context: Context, station: RadioBrowserStation) {
            val instance = activeInstance
            if (instance != null) {
                instance.playStationInternal(station)
            } else {
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = ACTION_PLAY_STATION
                    putExtra(EXTRA_STATION_UUID, station.stationuuid)
                    putExtra(EXTRA_STATION_NAME, station.name)
                    putExtra(EXTRA_STREAM_URL, station.getStreamUrl())
                    putExtra(EXTRA_TELUGU_NAME, station.teluguName)
                    putExtra(EXTRA_TAGS, station.tags)
                    putExtra(EXTRA_BITRATE, station.bitrate)
                    putExtra(EXTRA_CATEGORY, station.category)
                    putExtra(EXTRA_FREQUENCY, station.frequencyOrCity)
                }
                startServiceCompat(context, intent)
            }
        }

        fun togglePlay(context: Context) {
            val instance = activeInstance
            if (instance != null) {
                instance.togglePlayInternal()
            } else {
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = ACTION_TOGGLE_PLAY
                }
                startServiceCompat(context, intent)
            }
        }

        fun pause(context: Context) {
            val instance = activeInstance
            if (instance != null) {
                instance.pauseInternal()
            } else {
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = ACTION_PAUSE
                }
                startServiceCompat(context, intent)
            }
        }

        fun resume(context: Context) {
            val instance = activeInstance
            if (instance != null) {
                instance.resumeInternal()
            } else {
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = ACTION_RESUME
                }
                startServiceCompat(context, intent)
            }
        }

        fun stop(context: Context) {
            val instance = activeInstance
            if (instance != null) {
                instance.stopInternal()
                instance.stopForeground(STOP_FOREGROUND_REMOVE)
                instance.stopSelf()
            } else {
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            }
        }

        fun clearError() {
            _servicePlayerState.value = _servicePlayerState.value.copy(errorMessage = null)
        }

        private fun startServiceCompat(context: Context, intent: Intent) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error starting foreground service, falling back to startService", e)
                try {
                    context.startService(intent)
                } catch (e2: Exception) {
                    Log.e(TAG, "Cannot start service", e2)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        activeInstance = this
        createNotificationChannel()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("ShubhodayaApp/1.0 (Android; Mobile)")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(8000)
            .setReadTimeoutMs(10000)

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(DefaultDataSource.Factory(this, httpDataSourceFactory))

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(mediaSourceFactory)
            .apply {
                try {
                    setWakeMode(C.WAKE_MODE_LOCAL)
                } catch (e: Exception) {
                    Log.w(TAG, "Could not set wake mode: ${e.message}")
                }
            }
            .build()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _servicePlayerState.value = _servicePlayerState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                        updateForegroundNotification()
                    }
                    Player.STATE_READY -> {
                        _servicePlayerState.value = _servicePlayerState.value.copy(
                            isLoading = false,
                            isPlaying = player.isPlaying,
                            errorMessage = null
                        )
                        updateForegroundNotification()
                    }
                    Player.STATE_ENDED -> {
                        _servicePlayerState.value = _servicePlayerState.value.copy(
                            isLoading = false,
                            isPlaying = false
                        )
                        updateForegroundNotification()
                    }
                    Player.STATE_IDLE -> {
                        // Idle state
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _servicePlayerState.value = _servicePlayerState.value.copy(
                    isPlaying = isPlaying,
                    isLoading = false
                )
                updateForegroundNotification()
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Playback error: ${error.errorCodeName} (code=${error.errorCode}) - ${error.message}", error)
                _servicePlayerState.value = _servicePlayerState.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "Station unavailable, try another"
                )
                updateForegroundNotification()
            }
        })

        exoPlayer = player

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()

        // Immediate initial notification to satisfy foreground service contract
        val initialNotification = buildNotification(_servicePlayerState.value.currentStation, isPlaying = false, isLoading = false)
        startForegroundCompat(initialNotification)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_PLAY_STATION -> {
                val uuid = intent.getStringExtra(EXTRA_STATION_UUID) ?: ""
                val name = intent.getStringExtra(EXTRA_STATION_NAME) ?: "Telugu Radio"
                val streamUrl = intent.getStringExtra(EXTRA_STREAM_URL) ?: ""
                val teluguName = intent.getStringExtra(EXTRA_TELUGU_NAME) ?: ""
                val tags = intent.getStringExtra(EXTRA_TAGS) ?: ""
                val bitrate = intent.getIntExtra(EXTRA_BITRATE, 128)
                val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "POPULAR"
                val frequency = intent.getStringExtra(EXTRA_FREQUENCY) ?: ""

                val station = RadioBrowserStation(
                    stationuuid = uuid,
                    name = name,
                    url = streamUrl,
                    urlResolved = streamUrl,
                    tags = tags,
                    language = "telugu",
                    bitrate = bitrate,
                    teluguName = teluguName,
                    category = category,
                    frequencyOrCity = frequency
                )
                playStationInternal(station)
            }
            ACTION_TOGGLE_PLAY -> togglePlayInternal()
            ACTION_PAUSE -> pauseInternal()
            ACTION_RESUME -> resumeInternal()
            ACTION_STOP -> {
                stopInternal()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    fun playStationInternal(station: RadioBrowserStation) {
        val player = exoPlayer ?: return

        // If same station is already playing, toggle to pause
        if (_servicePlayerState.value.currentStation?.stationuuid == station.stationuuid && player.isPlaying) {
            pauseInternal()
            return
        }

        _servicePlayerState.value = RadioPlayerState(
            currentStation = station,
            isPlaying = false,
            isLoading = true,
            errorMessage = null
        )
        updateForegroundNotification()

        try {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(station.name)
                .setArtist(if (station.teluguName.isNotBlank()) station.teluguName else "Telugu Live Radio")
                .setDisplayTitle(station.name)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(station.getStreamUrl())
                .setMediaMetadata(mediaMetadata)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading media item for ${station.name}", e)
            _servicePlayerState.value = _servicePlayerState.value.copy(
                isPlaying = false,
                isLoading = false,
                errorMessage = "Station unavailable, try another"
            )
            updateForegroundNotification()
        }
    }

    fun togglePlayInternal() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            pauseInternal()
        } else {
            resumeInternal()
        }
    }

    fun pauseInternal() {
        exoPlayer?.pause()
        _servicePlayerState.value = _servicePlayerState.value.copy(isPlaying = false, isLoading = false)
        updateForegroundNotification()
    }

    fun resumeInternal() {
        val player = exoPlayer
        if (player != null && player.playbackState != Player.STATE_IDLE && player.mediaItemCount > 0) {
            player.play()
            _servicePlayerState.value = _servicePlayerState.value.copy(isPlaying = true, isLoading = false)
            updateForegroundNotification()
        } else {
            _servicePlayerState.value.currentStation?.let { playStationInternal(it) }
        }
    }

    fun stopInternal() {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        _servicePlayerState.value = RadioPlayerState(
            currentStation = null,
            isPlaying = false,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Telugu Radio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls and status for background radio streaming"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun updateForegroundNotification() {
        val currentState = _servicePlayerState.value
        val notification = buildNotification(
            station = currentState.currentStation,
            isPlaying = currentState.isPlaying,
            isLoading = currentState.isLoading
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        station: RadioBrowserStation?,
        isPlaying: Boolean,
        isLoading: Boolean
    ): Notification {
        val title = station?.name ?: "Telugu Radio / తెలుగు రేడియో"
        val subtitle = when {
            isLoading -> "Buffering / కనెక్ట్ అవుతోంది..."
            station?.teluguName?.isNotBlank() == true -> station.teluguName
            station?.frequencyOrCity?.isNotBlank() == true -> station.frequencyOrCity
            else -> "Live Radio Streaming"
        }

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toggleIntent = Intent(this, RadioPlaybackService::class.java).apply {
            action = ACTION_TOGGLE_PLAY
        }
        val togglePendingIntent = PendingIntent.getService(
            this,
            1,
            toggleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, RadioPlaybackService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentPendingIntent)
            .setOngoing(isPlaying || isLoading)
            .addAction(playPauseIcon, playPauseTitle, togglePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = exoPlayer
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        exoPlayer = null
        activeInstance = null
        _servicePlayerState.value = RadioPlayerState()
        super.onDestroy()
    }
}
