package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.klsr.radio.R
import com.klsr.radio.adapters.BlogPostAdapter
import com.klsr.radio.databinding.FragmentBlogBinding

class BlogFragment : Fragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlogBinding.bind(view)

        // Simple list with placeholder data
        val posts = listOf("Article 1", "Article 2", "Article 3")
        binding.blogRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.blogRecyclerView.adapter = BlogPostAdapter(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
