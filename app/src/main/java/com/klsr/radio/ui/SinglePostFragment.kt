package com.klsr.radio.ui

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.klsr.radio.R
import com.klsr.radio.data.BlogPostResponse
import com.klsr.radio.databinding.FragmentSinglePostBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SinglePostFragment : SafeFragment(R.layout.fragment_single_post) {
    private var _binding: FragmentSinglePostBinding? = null
    private val binding get() = _binding!!
    private val postId: Int by lazy { arguments?.getInt("postId", 0) ?: 0 }

    override fun onSafeViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSinglePostBinding.bind(view)
        binding.backBtn.setOnClickListener { parentFragmentManager.popBackStack() }
        loadPost()
    }

    private fun loadPost() {
        lifecycleScope.launch {
            val post = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://kingdomlifestyleradio.com/wp/wp-json/wp/v2/posts/$postId?_embed")
                    val conn = url.openConnection() as HttpURLConnection
                    val json = conn.inputStream.bufferedReader().readText()
                    Gson().fromJson(json, BlogPostResponse::class.java)
                } catch (e: Exception) { null }
            }
            post?.let {
                binding.postTitle.text = it.title.rendered
                binding.postDate.text = it.date.take(10)
                binding.postContent.text = Html.fromHtml(it.content.rendered, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
