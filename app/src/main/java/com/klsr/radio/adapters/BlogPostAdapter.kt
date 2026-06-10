package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klsr.radio.databinding.ItemBlogPostBinding

class BlogPostAdapter(private val items: List<String>) :
    RecyclerView.Adapter<BlogPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlogPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(private val binding: ItemBlogPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.blogTitle.text = text
        }
    }
}
