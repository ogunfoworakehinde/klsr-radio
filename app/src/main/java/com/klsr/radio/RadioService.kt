package com.klsr.radio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper

class RadioService : Service() {
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var currentStationIndex = 0

    companion object {
        const val CHANNEL_ID = "radio_playback"
        const val NOTIFICATION_ID = 101
        const val ACTION_PLAY_PAUSE = "com.klsr.radio.PLAY_PAUSE"
        const val EXTRA_STATION_INDEX = "station_index"
        val STATIONS = arrayOf(
            Station("English Gospel", "https://s3.voscast.com:9425/stream", "24/7 English Gospel Music"),
            Station("Yoruba Gospel", "https://s3.voscast.com:10745/stream", "Yoruba Language Worship"),
            Station("Praise Worship", "https://stream.zeno.fm/f3wvbbqmdg8uv", "Contemporary Praise")
        )
    }

    data class Station(val name: String, val url: String, val description: String)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        player = ExoPlayer.Builder(this).build()
        player.setMediaItem(MediaItem.fromUri(STATIONS[currentStationIndex].url))
        player.prepare()
        player.playWhenReady = true
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            else -> {
                intent?.getIntExtra(EXTRA_STATION_INDEX, -1)?.let { index ->
                    if (index in STATIONS.indices && index != currentStationIndex) {
                        currentStationIndex = index
                        player.setMediaItem(MediaItem.fromUri(STATIONS[index].url))
                        player.prepare()
                        player.playWhenReady = true
                    }
                }
            }
        }
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Radio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Shows current radio station" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val station = STATIONS[currentStationIndex]
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseAction = NotificationCompat.Action(
            if (player.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            if (player.isPlaying) "Pause" else "Play",
            PendingIntent.getService(
                this, 0,
                Intent(this, RadioService::class.java).apply { action = ACTION_PLAY_PAUSE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(station.name)
            .setContentText(station.description)
            .setContentIntent(openIntent)
            .setOngoing(player.isPlaying)
            .addAction(playPauseAction)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    inner class MediaSessionCallback : MediaSession.Callback {
        override fun onPlay() { player.play() }
        override fun onPause() { player.pause() }
        override fun onStop() { stopSelf() }
    }
}
