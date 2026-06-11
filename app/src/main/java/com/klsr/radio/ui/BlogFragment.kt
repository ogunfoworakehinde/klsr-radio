package com.klsr.radio.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.klsr.radio.R
import com.klsr.radio.adapters.BlogHeroSliderAdapter
import com.klsr.radio.data.BlogPost
import com.klsr.radio.databinding.FragmentBlogBinding

class BlogFragment : Fragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private var heroRunnable: Runnable? = null
    private var heroCurrentIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlogBinding.bind(view)

        // Hide other elements
        binding.blogRecyclerView.visibility = View.GONE
        binding.paginationControls.root.visibility = View.GONE
        binding.searchEditText.visibility = View.GONE

        // Dummy blog posts with local drawable
        val dummyPosts = listOf(
            BlogPost(1, "Title 1", "Excerpt 1", "2025-01-01", null, "Author", ""),
            BlogPost(2, "Title 2", "Excerpt 2", "2025-01-02", null, "Author", ""),
            BlogPost(3, "Title 3", "Excerpt 3", "2025-01-03", null, "Author", "")
        )

        setupHeroSlider(dummyPosts)
    }

    private fun setupHeroSlider(posts: List<BlogPost>) {
        if (posts.isEmpty()) return
        binding.blogHeroViewPager.visibility = View.VISIBLE
        binding.blogHeroDots.visibility = View.VISIBLE

        val adapter = BlogHeroSliderAdapter(posts) { postId ->
            // no-op for now
        }
        binding.blogHeroViewPager.adapter = adapter

        val dotsLayout = binding.blogHeroDots
        dotsLayout.removeAllViews()
        for (i in posts.indices) {
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
                startHeroAutoSlide()
            }
        })
        startHeroAutoSlide()
    }

    private fun startHeroAutoSlide() {
        heroRunnable?.let { handler.removeCallbacks(it) }
        val r = object : Runnable {
            override fun run() {
                if (!isAdded || heroCurrentIndex < 0) return
                val count = (binding.blogHeroViewPager.adapter?.itemCount ?: 0)
                if (count == 0) return
                heroCurrentIndex = (heroCurrentIndex + 1) % count
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
