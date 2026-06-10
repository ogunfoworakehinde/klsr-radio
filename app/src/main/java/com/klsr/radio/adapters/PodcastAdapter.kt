package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.PodcastEpisode
import com.klsr.radio.databinding.ItemPodcastBinding

class PodcastAdapter(
    private val items: List<PodcastEpisode>,
    private val onItemClick: (PodcastEpisode) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPodcastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemPodcastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(episode: PodcastEpisode) {
            binding.podcastTitle.text = episode.title
            binding.podcastDescription.text = episode.description
            episode.imageUrl?.let {
                Glide.with(binding.root).load(it).into(binding.podcastImage)
            }
            binding.root.setOnClickListener { onItemClick(episode) }
        }
    }
}
