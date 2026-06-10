package com.klsr.radio.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var currentSlide = 0
    private val images = listOf(R.drawable.herr1, R.drawable.herr2, R.drawable.herr3, R.drawable.herr4)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        binding?.let { b ->
            b.heroViewPager.adapter = HeroSliderAdapter(images)
            val dotsLayout: LinearLayout = b.dotsLayout
            dotsLayout.removeAllViews()
            for (i in images.indices) {
                val dot = ImageView(requireContext()).apply {
                    setImageResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
                    layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 8 }
                }
                dotsLayout.addView(dot)
            }
            b.heroViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentSlide = position
                    for (i in 0 until dotsLayout.childCount) {
                        (dotsLayout.getChildAt(i) as ImageView).setImageResource(
                            if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                        )
                    }
                    startAutoSlide()
                }
            })
            b.btnListenLive.setOnClickListener {
                val i = Intent(requireContext(), RadioService::class.java).apply { putExtra(RadioService.EXTRA_STATION_INDEX, 0) }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                    requireContext().startForegroundService(i)
                else requireContext().startService(i)
            }
            startAutoSlide()
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
                handler.postDelayed(this, 4000)
            }
        }
        runnable = r
        handler.postDelayed(r, 4000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        runnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }
}
