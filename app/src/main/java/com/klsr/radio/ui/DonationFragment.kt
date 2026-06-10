package com.klsr.radio.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentDonationBinding

class DonationFragment : SafeFragment(R.layout.fragment_donation) {
    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    override fun onSafeViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            _binding = FragmentDonationBinding.bind(view)
            binding.copyZenithBtn.setOnClickListener { copy("1229216755") }
            binding.copyGtbankNgnBtn.setOnClickListener { copy("0892125365") }
            binding.copyGtbankUsdBtn.setOnClickListener { copy("0892172060") }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun copy(text: String) {
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("account", text))
        Toast.makeText(requireContext(), "Copied: $text", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
