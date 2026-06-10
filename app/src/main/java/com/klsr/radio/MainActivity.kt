package com.klsr.radio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.common.util.concurrent.MoreExecutors
import com.klsr.radio.databinding.ActivityMainBinding
import com.klsr.radio.ui.ChannelSwitcherFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // MediaController – wrap in try/catch so it never crashes the app
        try {
            val sessionToken = SessionToken(this, ComponentName(this, RadioService::class.java))
            val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
            controllerFuture.addListener({
                try {
                    mediaController = controllerFuture.get()
                    mediaController?.let { mc ->
                        mc.addListener(PlayerListener())
                        updatePlayerBar(mc)
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Failed to get MediaController", e)
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            Log.e("MainActivity", "MediaController setup failed", e)
        }

        // Player bar buttons
        binding.playerBar.btnPlayPause.setOnClickListener {
            mediaController?.let { mc ->
                try {
                    if (mc.isPlaying) mc.pause() else mc.play()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Play/pause failed", e)
                }
            }
        }
        binding.playerBar.btnPrev.setOnClickListener { switchStation(-1) }
        binding.playerBar.btnNext.setOnClickListener { switchStation(1) }
        binding.playerBar.btnChannelSwitcher.setOnClickListener {
            val bottomSheet = ChannelSwitcherFragment { index ->
                switchStation(index - appState.currentStation)
            }
            bottomSheet.show(supportFragmentManager, "ChannelSwitcher")
        }
    }

    private var appState = object { var currentStation = 0 }

    private fun switchStation(delta: Int) {
        val newIndex = (appState.currentStation + delta + RadioService.STATIONS.size) % RadioService.STATIONS.size
        appState.currentStation = newIndex
        val intent = Intent(this, RadioService::class.java).apply {
            putExtra(RadioService.EXTRA_STATION_INDEX, newIndex)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start service", e)
        }
    }

    private fun updatePlayerBar(mc: MediaController?) {
        val station = RadioService.STATIONS[appState.currentStation]
        binding.playerBar.stationName.text = station.name
        binding.playerBar.stationDesc.text = station.description
        val icon = if (mc?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerBar.btnPlayPause.setImageResource(icon)
    }

    inner class PlayerListener : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayerBar(mediaController)
        }
        override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
            val index = RadioService.STATIONS.indexOfFirst { it.url == mediaItem?.localConfiguration?.uri.toString() }
            if (index != -1) appState.currentStation = index
            updatePlayerBar(mediaController)
        }
    }
}
