package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.BlogPost
import com.klsr.radio.databinding.ItemBlogHeroSlideBinding

class BlogHeroAdapter(
    private val posts: List<BlogPost>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<BlogHeroAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemBlogHeroSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class VH(private val b: ItemBlogHeroSlideBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(post: BlogPost) {
            b.heroTitle.text = post.title
            b.heroDate.text = post.date
            post.featuredMediaUrl?.let {
                Glide.with(b.root.context).load(it).into(b.heroImage)
            }
            b.heroReadMoreBtn.setOnClickListener { onClick(post.id) }
        }
    }
}
