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

class DonationFragment : Fragment(R.layout.fragment_donation) {
    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDonationBinding.bind(view)

        binding.copyZenithBtn.setOnClickListener {
            copyText("1229216755")
        }
        binding.copyGtbankNgnBtn.setOnClickListener {
            copyText("0892125365")
        }
        binding.copyGtbankUsdBtn.setOnClickListener {
            copyText("0892172060")
        }
    }

    private fun copyText(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("account", text))
        Toast.makeText(requireContext(), "Copied: $text", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
