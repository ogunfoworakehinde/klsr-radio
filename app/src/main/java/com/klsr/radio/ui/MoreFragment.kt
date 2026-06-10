package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentMoreBinding

class MoreFragment : SafeFragment(R.layout.fragment_more) {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onSafeViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            _binding = FragmentMoreBinding.bind(view)
            binding.aboutBtn.setOnClickListener { findNavController().navigate(R.id.aboutFragment) }
            binding.donateBtn.setOnClickListener { findNavController().navigate(R.id.donationFragment) }
            binding.contactBtn.setOnClickListener { findNavController().navigate(R.id.contactFragment) }
            binding.settingsBtn.setOnClickListener { findNavController().navigate(R.id.settingsFragment) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
