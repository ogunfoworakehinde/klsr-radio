package com.klsr.radio.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.klsr.radio.R
import com.klsr.radio.adapters.HeroSliderAdapter
import com.klsr.radio.databinding.FragmentBlogBinding

class BlogFragment : Fragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var heroRunnable: Runnable? = null
    private var heroCurrentIndex = 0
    // Use the same images as the home page
    private val images = listOf(R.drawable.herr1, R.drawable.herr2, R.drawable.herr3, R.drawable.herr4)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlogBinding.bind(view)

        // Hide search + posts
        binding.blogRecyclerView.visibility = View.GONE
        binding.paginationControls.root.visibility = View.GONE
        binding.searchEditText.visibility = View.GONE

        // Show hero with static images
        binding.blogHeroViewPager.visibility = View.VISIBLE
        binding.blogHeroDots.visibility = View.VISIBLE

        val adapter = HeroSliderAdapter(images)
        binding.blogHeroViewPager.adapter = adapter

        val dotsLayout = binding.blogHeroDots
        dotsLayout.removeAllViews()
        for (i in images.indices) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
                layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 8 }
            }
            dotsLayout.addView(dot)
        }
        binding.blogHeroViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                heroCurrentIndex = position
                for (i in 0 until dotsLayout.childCount) {
                    (dotsLayout.getChildAt(i) as? ImageView)?.setImageResource(
                        if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                    )
                }
                startAutoSlide()
            }
        })
        startAutoSlide()
    }

    private fun startAutoSlide() {
        heroRunnable?.let { handler.removeCallbacks(it) }
        val r = object : Runnable {
            override fun run() {
                if (!isAdded) return
                heroCurrentIndex = (heroCurrentIndex + 1) % images.size
                binding.blogHeroViewPager.setCurrentItem(heroCurrentIndex, true)
                handler.postDelayed(this, 5000)
            }
        }
        heroRunnable = r
        handler.postDelayed(r, 5000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        heroRunnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }
}
