package com.klsr.radio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.klsr.radio.R

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Team member images (loaded from drawable)
        view.findViewById<ImageView>(R.id.imageTeam1).setImageResource(R.drawable.team1)
        view.findViewById<ImageView>(R.id.imageTeam2).setImageResource(R.drawable.manager)
        view.findViewById<ImageView>(R.id.imageTeam3).setImageResource(R.drawable.voice)
    }
}
