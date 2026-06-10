package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentMoreMenuBinding

class MoreMenuFragment : Fragment(R.layout.fragment_more_menu) {
    private var _binding: FragmentMoreMenuBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoreMenuBinding.bind(view)

        binding.aboutBtn.setOnClickListener { findNavController().navigate(R.id.aboutFragment) }
        binding.donateBtn.setOnClickListener { findNavController().navigate(R.id.donationFragment) }
        binding.contactBtn.setOnClickListener { findNavController().navigate(R.id.contactFragment) }
        binding.settingsBtn.setOnClickListener { findNavController().navigate(R.id.settingsFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
