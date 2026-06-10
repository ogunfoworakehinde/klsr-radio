package com.klsr.radio.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.klsr.radio.R
import com.klsr.radio.RadioService
import com.klsr.radio.adapters.HeroSliderAdapter
import com.klsr.radio.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var currentSlide = 0
    private val images = listOf(R.drawable.herr1, R.drawable.herr2, R.drawable.herr3, R.drawable.herr4)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Set up the adapter
        val adapter = HeroSliderAdapter(images)
        binding?.heroViewPager?.apply {
            this.adapter = adapter
            // Fade transition
            setPageTransformer(FadePageTransformer())
            // Start auto-sliding
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentSlide = position
                    startAutoSlide()
                }
            })
            startAutoSlide()
        }

        // Listen Live button
        binding?.btnListenLive?.setOnClickListener {
            val intent = Intent(requireContext(), RadioService::class.java).apply {
                putExtra(RadioService.EXTRA_STATION_INDEX, 0)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                requireContext().startForegroundService(intent)
            else requireContext().startService(intent)
        }
    }

    private fun startAutoSlide() {
        runnable?.let { handler.removeCallbacks(it) }
        val r = object : Runnable {
            override fun run() {
                binding?.heroViewPager?.let {
                    currentSlide = (currentSlide + 1) % images.size
                    it.setCurrentItem(currentSlide, true)
                }
                handler.postDelayed(this, 5000) // 5 seconds
            }
        }
        runnable = r
        handler.postDelayed(r, 5000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        runnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }

    /** Simple fade transformer */
    inner class FadePageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.apply {
                alpha = when {
                    position < -1 -> 0f
                    position <= 1 -> 1 - Math.abs(position)
                    else -> 0f
                }
                translationX = 0f // no parallax
            }
        }
    }
}
