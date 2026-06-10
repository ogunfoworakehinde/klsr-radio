package com.klsr.radio.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.klsr.radio.MainActivity
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        prefs = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)

        // --- Load current settings ---
        // Theme spinner (0 = Deep Blue, 1 = Dark, 2 = Light)
        val currentTheme = prefs.getString("app_theme", "deep_blue") ?: "deep_blue"
        binding.themeSpinner.setSelection(
            when (currentTheme) {
                "dark" -> 1
                "light" -> 2
                else -> 0
            }
        )

        binding.radioVolumeSeekBar.progress = prefs.getInt("radio_volume", 100)
        binding.podcastVolumeSeekBar.progress = prefs.getInt("podcast_volume", 100)
        binding.autoPlaySwitch.isChecked = prefs.getBoolean("auto_play", true)
        binding.notificationsSwitch.isChecked = prefs.getBoolean("notifications", true)
        binding.dataSaverSwitch.isChecked = prefs.getBoolean("data_saver", false)

        // --- Save button ---
        binding.saveSettingsBtn.setOnClickListener {
            saveAllSettings()
        }

        // --- Reset button ---
        binding.resetSettingsBtn.setOnClickListener {
            resetToDefault()
        }
    }

    private fun saveAllSettings() {
        val editor = prefs.edit()
        val themeValue = when (binding.themeSpinner.selectedItemPosition) {
            1 -> "dark"
            2 -> "light"
            else -> "deep_blue"
        }
        editor.putString("app_theme", themeValue)
        editor.putInt("radio_volume", binding.radioVolumeSeekBar.progress)
        editor.putInt("podcast_volume", binding.podcastVolumeSeekBar.progress)
        editor.putBoolean("auto_play", binding.autoPlaySwitch.isChecked)
        editor.putBoolean("notifications", binding.notificationsSwitch.isChecked)
        editor.putBoolean("data_saver", binding.dataSaverSwitch.isChecked)
        editor.apply()

        // Apply theme immediately
        applyTheme(themeValue)
        Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()

        // Restart MainActivity to apply theme fully
        requireActivity().finish()
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private fun resetToDefault() {
        prefs.edit().clear().apply()
        // Reload UI with defaults
        binding.themeSpinner.setSelection(0)
        binding.radioVolumeSeekBar.progress = 100
        binding.podcastVolumeSeekBar.progress = 100
        binding.autoPlaySwitch.isChecked = true
        binding.notificationsSwitch.isChecked = true
        binding.dataSaverSwitch.isChecked = false
        applyTheme("deep_blue")
        Toast.makeText(requireContext(), "Settings reset to default", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private fun applyTheme(theme: String) {
        val nightMode = when (theme) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // Deep Blue = system default
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
