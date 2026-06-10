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
import com.klsr.radio.utils.SafeImageHelper

class DonationFragment : Fragment(R.layout.fragment_donation) {
    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDonationBinding.bind(view)
        binding?.let { b ->
            SafeImageHelper.load(context, b.zenithLogo, R.drawable.zenith_bank_logo)
            SafeImageHelper.load(context, b.gtbankNgnLogo, R.drawable.gtbank_logo)
            SafeImageHelper.load(context, b.gtbankUsdLogo, R.drawable.gtbank_logo)
            b.copyZenithBtn.setOnClickListener { copy("1229216755") }
            b.copyGtbankNgnBtn.setOnClickListener { copy("0892125365") }
            b.copyGtbankUsdBtn.setOnClickListener { copy("0892172060") }
        }
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
