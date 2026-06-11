package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentSinglePostBinding

class SinglePostFragment : Fragment(R.layout.fragment_single_post) {
    private var _binding: FragmentSinglePostBinding? = null
    private val binding get() = _binding!!
    private val postId: Int by lazy { arguments?.getInt("postId", 0) ?: 0 }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSinglePostBinding.bind(view)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.postTitle.text = "Post $postId"
        binding.postContent.text = "This is the content of post $postId. (Placeholder)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
