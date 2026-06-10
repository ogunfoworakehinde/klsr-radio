package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentAboutBinding
import com.klsr.radio.utils.SafeImageHelper

class AboutFragment : Fragment(R.layout.fragment_about) {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)
        binding?.let { b ->
            SafeImageHelper.load(context, b.imageTeam1, R.drawable.team1)
            SafeImageHelper.load(context, b.imageTeam2, R.drawable.manager)
            SafeImageHelper.load(context, b.imageTeam3, R.drawable.voice)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
