package com.klsr.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.klsr.radio.data.PodcastEpisode
import com.klsr.radio.databinding.ItemPodcastBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PodcastAdapter(
    private val items: List<PodcastEpisode>,
    private val onPlayClick: (PodcastEpisode) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPodcastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class VH(private val b: ItemPodcastBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(episode: PodcastEpisode) {
            b.podcastTitle.text = episode.title
            b.podcastDescription.text = episode.description ?: ""

            // Format date
            try {
                val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                episode.pubDate?.let { dateStr ->
                    val date = inputFormat.parse(dateStr)
                    if (date != null) {
                        b.podcastDate.text = outputFormat.format(date)
                    } else {
                        b.podcastDate.text = dateStr.take(10)
                    }
                }
            } catch (e: Exception) {
                b.podcastDate.text = episode.pubDate?.take(10) ?: ""
            }

            // Load image with Glide
            episode.imageUrl?.let {
                Glide.with(b.root.context).load(it).into(b.podcastImage)
            } ?: run {
                b.podcastImage.setBackgroundColor(0xFF1a235c.toInt())
            }

            b.btnPlayEpisode.setOnClickListener {
                onPlayClick(episode)
            }
        }
    }
}
