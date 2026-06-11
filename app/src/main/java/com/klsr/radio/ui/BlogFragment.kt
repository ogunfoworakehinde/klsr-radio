package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentBlogBinding

class BlogFragment : Fragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBlogBinding.bind(view)
        binding.blogStatus.text = "Blog loaded without crash!"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
