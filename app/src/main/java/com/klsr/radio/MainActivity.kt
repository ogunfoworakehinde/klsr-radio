package com.klsr.radio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
    private var currentStation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostFragment.navController)

        try {
            val token = SessionToken(this, ComponentName(this, RadioService::class.java))
            val future = MediaController.Builder(this, token).buildAsync()
            future.addListener({
                try {
                    mediaController = future.get()
                    mediaController?.addListener(PlayerListener())
                    updatePlayerBar()
                } catch (e: Exception) { Log.e("MainActivity", "MC get failed", e) }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) { Log.e("MainActivity", "SessionToken failed", e) }

        binding.playerBar.btnPlayPause.setOnClickListener {
            mediaController?.let { mc ->
                if (mc.isPlaying) mc.pause() else {
                    ensureServiceStarted()
                    mc.play()
                }
            } ?: ensureServiceStarted()
        }
        binding.playerBar.btnPrev.setOnClickListener { switchStation(-1) }
        binding.playerBar.btnNext.setOnClickListener { switchStation(1) }
        binding.playerBar.btnChannelSwitcher.setOnClickListener {
            val bottomSheet = ChannelSwitcherFragment()
            bottomSheet.show(supportFragmentManager, "ChannelSwitcher")
            // Listen for result
            supportFragmentManager.setFragmentResultListener(ChannelSwitcherFragment.REQUEST_KEY, this) { _, bundle ->
                val index = bundle.getInt(ChannelSwitcherFragment.RESULT_INDEX, -1)
                if (index != -1 && index != currentStation) {
                    switchStation(index - currentStation)
                }
            }
        }
    }

    private fun ensureServiceStarted() {
        val i = Intent(this, RadioService::class.java).apply { putExtra(RadioService.EXTRA_STATION_INDEX, currentStation) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
    }

    private fun switchStation(delta: Int) {
        currentStation = (currentStation + delta + RadioService.STATIONS.size) % RadioService.STATIONS.size
        val i = Intent(this, RadioService::class.java).apply { putExtra(RadioService.EXTRA_STATION_INDEX, currentStation) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
    }

    private fun updatePlayerBar(mc: MediaController? = mediaController) {
        val s = RadioService.STATIONS[currentStation]
        binding.playerBar.stationName.text = s.name
        binding.playerBar.stationDesc.text = s.desc
        val icon = if (mc?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerBar.btnPlayPause.setImageResource(icon)
    }

    inner class PlayerListener : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) { updatePlayerBar() }
        override fun onMediaItemTransition(item: androidx.media3.common.MediaItem?, reason: Int) {
            val idx = RadioService.STATIONS.indexOfFirst { it.url == item?.localConfiguration?.uri.toString() }
            if (idx != -1) currentStation = idx
            updatePlayerBar()
        }
    }
}
