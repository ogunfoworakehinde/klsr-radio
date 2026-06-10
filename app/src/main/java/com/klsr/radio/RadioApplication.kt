package com.klsr.radio

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.klsr.radio.utils.CrashHandler

class RadioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Install global crash handler FIRST
        CrashHandler.init(this)

        try {
            val prefs = getSharedPreferences("settings", MODE_PRIVATE)
            val dark = prefs.getBoolean("dark_mode", false)
            AppCompatDelegate.setDefaultNightMode(
                if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        } catch (_: Exception) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
