package com.klsr.radio.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.klsr.radio.R
import com.klsr.radio.adapters.BlogPostAdapter
import com.klsr.radio.data.BlogPost
import com.klsr.radio.databinding.FragmentBlogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class BlogFragment : Fragment(R.layout.fragment_blog) {
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!
    private var allPosts = emptyList<BlogPost>()
    private var currentPage = 1
    private var totalPages = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            _binding = FragmentBlogBinding.bind(view)
            binding.blogRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            loadPosts(1)

            binding.searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterPosts(s?.toString() ?: "")
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            binding.paginationControls.prevPageBtn.setOnClickListener {
                if (currentPage > 1) loadPosts(currentPage - 1)
            }
            binding.paginationControls.nextPageBtn.setOnClickListener {
                if (currentPage < totalPages) loadPosts(currentPage + 1)
            }
        } catch (e: Exception) {
            Log.e("BlogFragment", "onViewCreated failed", e)
        }
    }

    private fun loadPosts(page: Int) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://kingdomlifestyleradio.com/wp/wp-json/wp/v2/posts?per_page=6&page=$page&_embed")
                    val conn = url.openConnection() as HttpURLConnection
                    val json = conn.inputStream.bufferedReader().readText()
                    val arr = JSONArray(json)
                    val posts = mutableListOf<BlogPost>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val id = obj.getInt("id")
                        val title = obj.getJSONObject("title").getString("rendered")
                        val excerpt = obj.getJSONObject("excerpt").getString("rendered").replace(Regex("<[^>]+>"), "")
                        val date = obj.getString("date").substring(0, 10)
                        val imageUrl = obj.optJSONObject("_embedded")?.optJSONArray("wp:featuredmedia")
                            ?.optJSONObject(0)?.optString("source_url")
                        val author = obj.optJSONObject("_embedded")?.optJSONArray("author")
                            ?.optJSONObject(0)?.optString("name") ?: "Admin"
                        posts.add(BlogPost(id, title, excerpt, date, imageUrl, author))
                    }
                    totalPages = conn.getHeaderField("X-WP-TotalPages")?.toIntOrNull() ?: 1
                    currentPage = page
                    Pair(posts, totalPages)
                } catch (e: Exception) {
                    Log.e("BlogFragment", "API load failed", e)
                    Pair(emptyList<BlogPost>(), 1)
                }
            }
            allPosts = result.first
            binding.blogRecyclerView.adapter = BlogPostAdapter(allPosts) { postId ->
                findNavController().navigate(R.id.singlePostFragment, Bundle().apply { putInt("postId", postId) })
            }
            binding.paginationControls.pageIndicator.text = "Page $currentPage of $totalPages"
            binding.paginationControls.root.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
        }
    }

    private fun filterPosts(query: String) {
        val filtered = if (query.isBlank()) allPosts
        else allPosts.filter { it.title.contains(query, ignoreCase = true) || it.excerpt.contains(query, ignoreCase = true) }
        binding.blogRecyclerView.adapter = BlogPostAdapter(filtered) { postId ->
            findNavController().navigate(R.id.singlePostFragment, Bundle().apply { putInt("postId", postId) })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
