package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.BlogPost
import com.klsr.radio.databinding.ItemBlogPostBinding

class BlogPostAdapter(private val items: List<BlogPost>, private val onClick: (Int) -> Unit) : RecyclerView.Adapter<BlogPostAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemBlogPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }
    override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(items[position]) }
    override fun getItemCount() = items.size
    inner class VH(private val b: ItemBlogPostBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(post: BlogPost) {
            b.blogTitle.text = post.title
            b.blogExcerpt.text = post.excerpt
            post.featuredMediaUrl?.let { Glide.with(b.root).load(it).into(b.blogImage) }
            b.root.setOnClickListener { onClick(post.id) }
        }
    }
}
