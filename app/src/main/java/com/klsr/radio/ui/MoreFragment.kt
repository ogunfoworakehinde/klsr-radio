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
        binding.aboutBtn.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_about)
        }
        binding.donateBtn.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_donation)
        }
        binding.contactBtn.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_contact)
        }
        binding.settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_settings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
