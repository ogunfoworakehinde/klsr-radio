package com.klsr.radio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.common.util.concurrent.MoreExecutors
import com.klsr.radio.databinding.ActivityMainBinding
import com.klsr.radio.ui.ChannelSwitcherFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var mediaController: MediaController? = null
    private var currentStation = 0
    private var isPlaying = false // local tracking for immediate UI update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Bottom navigation – manual selection + highlight color
        setupBottomNav()

        // Sync bottom nav with current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.menu.findItem(destination.id)?.isChecked = true
        }

        // Settings button in toolbar
        binding.btnSettings.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }

        // MediaController
        try {
            val token = SessionToken(this, ComponentName(this, RadioService::class.java))
            val future = MediaController.Builder(this, token).buildAsync()
            future.addListener({
                try {
                    mediaController = future.get()
                    mediaController?.addListener(PlayerListener())
                    isPlaying = mediaController?.isPlaying ?: false
                    updatePlayerBar()
                } catch (e: Exception) { Log.e("MainActivity", "MC get failed", e) }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) { Log.e("MainActivity", "SessionToken failed", e) }

        // Player bar buttons
        binding.playerBar.btnPlayPause.setOnClickListener {
            mediaController?.let { mc ->
                if (mc.isPlaying) {
                    mc.pause()
                } else {
                    ensureServiceStarted()
                    mc.play()
                }
                isPlaying = !isPlaying
                updatePlayPauseIcon()
            } ?: run {
                ensureServiceStarted()
                isPlaying = true
                updatePlayPauseIcon()
            }
        }
        binding.playerBar.btnPrev.setOnClickListener { switchStation(-1) }
        binding.playerBar.btnNext.setOnClickListener { switchStation(1) }
        binding.playerBar.btnChannelSwitcher.setOnClickListener {
            val bottomSheet = ChannelSwitcherFragment()
            bottomSheet.show(supportFragmentManager, "ChannelSwitcher")
            supportFragmentManager.setFragmentResultListener(ChannelSwitcherFragment.REQUEST_KEY, this) { _, bundle ->
                val index = bundle.getInt(ChannelSwitcherFragment.RESULT_INDEX, -1)
                if (index != -1 && index != currentStation) {
                    switchStation(index - currentStation)
                }
            }
        }
    }

    private fun setupBottomNav() {
        // Determine active highlight color based on night mode
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isDark = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES

        val activeColor = if (isDark) Color.parseColor("#FFD700") else Color.parseColor("#4fc3f7") // gold / light blue
        val inactiveColor = Color.WHITE

        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )
        val colors = intArrayOf(activeColor, inactiveColor)
        val colorStateList = ColorStateList(states, colors)

        binding.bottomNav.itemIconTintList = colorStateList
        binding.bottomNav.itemTextColor = colorStateList

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.moreMenuItem -> {
                    showMoreMenu()
                    false
                }
                else -> {
                    navController.navigate(item.itemId, null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }
            }
        }
    }

    private fun showMoreMenu() {
        val menuItemView = binding.bottomNav.findViewById<android.view.View>(R.id.moreMenuItem)
        val popup = PopupMenu(this, menuItemView)
        popup.menuInflater.inflate(R.menu.more_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_about -> navController.navigate(R.id.aboutFragment)
                R.id.action_donate -> navController.navigate(R.id.donationFragment)
                R.id.action_contact -> navController.navigate(R.id.contactFragment)
                R.id.action_settings -> navController.navigate(R.id.settingsFragment)
            }
            true
        }
        popup.show()
    }

    private fun ensureServiceStarted() {
        val i = Intent(this, RadioService::class.java).apply { putExtra(RadioService.EXTRA_STATION_INDEX, currentStation) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
    }

    private fun switchStation(delta: Int) {
        currentStation = (currentStation + delta + RadioService.STATIONS.size) % RadioService.STATIONS.size
        val i = Intent(this, RadioService::class.java).apply { putExtra(RadioService.EXTRA_STATION_INDEX, currentStation) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
        updatePlayerBar()
    }

    private fun updatePlayerBar(mc: MediaController? = mediaController) {
        val s = RadioService.STATIONS[currentStation]
        binding.playerBar.stationName.text = s.name
        binding.playerBar.stationDesc.text = s.desc
        updatePlayPauseIcon()
    }

    private fun updatePlayPauseIcon() {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerBar.btnPlayPause.setImageResource(icon)
    }

    inner class PlayerListener : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
            updatePlayPauseIcon()
        }
        override fun onMediaItemTransition(item: androidx.media3.common.MediaItem?, reason: Int) {
            val idx = RadioService.STATIONS.indexOfFirst { it.url == item?.localConfiguration?.uri.toString() }
            if (idx != -1) currentStation = idx
            updatePlayerBar()
        }
    }
}
