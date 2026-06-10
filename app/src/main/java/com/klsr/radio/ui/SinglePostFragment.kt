package com.klsr.radio.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentSinglePostBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SinglePostFragment : Fragment(R.layout.fragment_single_post) {
    private var _binding: FragmentSinglePostBinding? = null
    private val binding get() = _binding!!
    private val postId: Int by lazy { arguments?.getInt("postId", 0) ?: 0 }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSinglePostBinding.bind(view)

        binding.backBtn.setOnClickListener { findNavController().popBackStack() }
        loadPost()
    }

    private fun loadPost() {
        lifecycleScope.launch {
            val post = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://kingdomlifestyleradio.com/wp/wp-json/wp/v2/posts/$postId?_embed")
                    val conn = url.openConnection() as HttpURLConnection
                    val json = conn.inputStream.bufferedReader().readText()
                    val obj = JSONObject(json)
                    obj
                } catch (e: Exception) {
                    null
                }
            }
            post?.let {
                val title = it.getJSONObject("title").getString("rendered")
                val content = it.getJSONObject("content").getString("rendered")
                val date = it.getString("date").substring(0, 10)
                binding.postTitle.text = title
                binding.postDate.text = date
                binding.postContent.text = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
