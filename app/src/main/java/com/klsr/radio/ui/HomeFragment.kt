package com.klsr.radio.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.klsr.radio.R
import com.klsr.radio.RadioService
import com.klsr.radio.adapters.HeroSliderAdapter
import com.klsr.radio.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images = listOf(
            R.drawable.herr1, R.drawable.herr2, R.drawable.herr3, R.drawable.herr4
        )
        binding.heroViewPager.adapter = HeroSliderAdapter(images)

        // Dots
        val dotsLayout: LinearLayout = binding.dotsLayout
        dotsLayout.removeAllViews()
        for (i in images.indices) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
                layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 8 }
            }
            dotsLayout.addView(dot)
        }
        binding.heroViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0 until dotsLayout.childCount) {
                    (dotsLayout.getChildAt(i) as ImageView).setImageResource(
                        if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                    )
                }
            }
        })

        binding.btnListenLive.setOnClickListener {
            val intent = Intent(requireContext(), RadioService::class.java).apply {
                action = RadioService.ACTION_PLAY_PAUSE
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent)
            } else {
                requireContext().startService(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
