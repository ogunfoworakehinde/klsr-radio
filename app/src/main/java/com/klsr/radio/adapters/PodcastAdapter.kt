package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.PodcastEpisode
import com.klsr.radio.databinding.ItemPodcastBinding

class PodcastAdapter(private val items: List<PodcastEpisode>, private val onClick: (PodcastEpisode) -> Unit) :
    RecyclerView.Adapter<PodcastAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPodcastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }
    override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(items[position]) }
    override fun getItemCount() = items.size
    inner class VH(private val b: ItemPodcastBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(ep: PodcastEpisode) {
            b.podcastTitle.text = ep.title
            b.podcastDescription.text = ep.description
            ep.imageUrl?.let { Glide.with(b.root).load(it).into(b.podcastImage) }
            b.root.setOnClickListener { onClick(ep) }
        }
    }
}
