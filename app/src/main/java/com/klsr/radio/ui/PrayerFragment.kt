package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentPrayerBinding

class PrayerFragment : Fragment(R.layout.fragment_prayer) {
    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPrayerBinding.bind(view)

        binding.submitPrayerBtn.setOnClickListener {
            // In a real app, send via EmailJS; here just show toast
            Toast.makeText(requireContext(), "Prayer request submitted (demo)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
