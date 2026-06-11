package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentMoreBinding

class MoreFragment : Fragment(R.layout.fragment_more) {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoreBinding.bind(view)

        binding.btnAbout.setOnClickListener { findNavController().navigate(R.id.aboutFragment) }
        binding.btnDonate.setOnClickListener { findNavController().navigate(R.id.donationFragment) }
        binding.btnContact.setOnClickListener { findNavController().navigate(R.id.contactFragment) }
        binding.btnSettings.setOnClickListener { findNavController().navigate(R.id.settingsFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
