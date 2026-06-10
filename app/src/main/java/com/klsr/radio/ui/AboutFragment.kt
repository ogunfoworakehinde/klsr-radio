package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {
    private var _binding: FragmentAboutBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)
        binding?.imageTeam1?.setImageResource(R.drawable.team1)
        binding?.imageTeam2?.setImageResource(R.drawable.manager)
        binding?.imageTeam3?.setImageResource(R.drawable.voice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
