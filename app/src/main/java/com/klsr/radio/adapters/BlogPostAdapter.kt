package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.BlogPost
import com.klsr.radio.databinding.ItemBlogPostBinding

class BlogPostAdapter(
    private val items: List<BlogPost>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<BlogPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlogPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemBlogPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: BlogPost) {
            binding.blogTitle.text = post.title
            binding.blogExcerpt.text = post.excerpt
            post.featuredMediaUrl?.let {
                Glide.with(binding.root).load(it).into(binding.blogImage)
            }
            binding.root.setOnClickListener { onItemClick(post.id) }
        }
    }
}
