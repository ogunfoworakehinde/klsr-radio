package com.klsr.radio.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.klsr.radio.R
import com.klsr.radio.adapters.BlogPostAdapter
import com.klsr.radio.data.BlogPost
import com.klsr.radio.data.BlogPostResponse
import com.klsr.radio.databinding.FragmentBlogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class BlogFragment : SafeFragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!
    private var allPosts = emptyList<BlogPost>()
    private var currentPage = 1
    private var totalPages = 1

    override fun onSafeViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentBlogBinding.bind(view)
        binding.blogRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadPosts(1)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filter(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.paginationControls.prevPageBtn.setOnClickListener { if (currentPage > 1) loadPosts(currentPage - 1) }
        binding.paginationControls.nextPageBtn.setOnClickListener { if (currentPage < totalPages) loadPosts(currentPage + 1) }
    }

    private fun loadPosts(page: Int) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://kingdomlifestyleradio.com/wp/wp-json/wp/v2/posts?per_page=6&page=$page&_embed")
                    val conn = url.openConnection() as HttpURLConnection
                    val json = conn.inputStream.bufferedReader().readText()
                    val listType = TypeToken.getParameterized(List::class.java, BlogPostResponse::class.java).type
                    val posts: List<BlogPostResponse> = Gson().fromJson(json, listType)
                    val total = conn.getHeaderField("X-WP-TotalPages")?.toIntOrNull() ?: 1
                    val items = posts.map { p ->
                        val imgUrl = p.embedded?.wpFeaturedmedia?.getOrNull(0)?.sourceUrl
                        val author = p.embedded?.author?.getOrNull(0)?.name ?: "Admin"
                        BlogPost(p.id, p.title.rendered, p.excerpt.rendered.replace(Regex("<[^>]+>"), "").take(150) + "...", p.date.take(10), imgUrl, author, p.content.rendered)
                    }
                    Pair(items, total)
                } catch (e: Exception) { Pair(emptyList<BlogPost>(), 1) }
            }
            allPosts = result.first
            totalPages = result.second
            currentPage = page
            binding.blogRecyclerView.adapter = BlogPostAdapter(allPosts) { postId ->
                findNavController().navigate(R.id.singlePostFragment, Bundle().apply { putInt("postId", postId) })
            }
            binding.paginationControls.pageIndicator.text = "Page $page of $totalPages"
            binding.paginationControls.root.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
        }
    }

    private fun filter(query: String) {
        val filtered = if (query.isBlank()) allPosts else allPosts.filter { it.title.contains(query, ignoreCase = true) || it.excerpt.contains(query, ignoreCase = true) }
        binding.blogRecyclerView.adapter = BlogPostAdapter(filtered) { postId ->
            findNavController().navigate(R.id.singlePostFragment, Bundle().apply { putInt("postId", postId) })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
