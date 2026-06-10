package com.klsr.radio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.klsr.radio.R
import com.klsr.radio.adapters.PodcastAdapter
import com.klsr.radio.data.PodcastEpisode
import com.klsr.radio.databinding.FragmentPodcastBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class PodcastFragment : Fragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            _binding = FragmentPodcastBinding.bind(view)
            binding.podcastHeroImage.setImageResource(R.drawable.hepp)
            binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            loadPodcasts()

            binding.playerLayout.btnPodcastPlayPause.setOnClickListener {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.pause() else mp.start()
                    updatePlayPauseIcon()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePlayPauseIcon() {
        _binding?.let {
            val icon = if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
            it.playerLayout.btnPodcastPlayPause.setImageResource(icon)
        }
    }

    private fun loadPodcasts() {
        lifecycleScope.launch {
            val episodes = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://anchor.fm/s/1d6ad87c/podcast/rss")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    val text = conn.inputStream.bufferedReader().readText()
                    val items = text.split("<item>").drop(1)
                    items.mapNotNull { item ->
                        try {
                            val title = Regex("<title><!\\[CDATA\\[(.*?)\\]\\]></title>").find(item)?.groupValues?.get(1) ?: ""
                            val desc = Regex("<description><!\\[CDATA\\[(.*?)\\]\\]></description>").find(item)?.groupValues?.get(1) ?: ""
                            val audio = Regex("<enclosure url=\"(.*?)\"").find(item)?.groupValues?.get(1) ?: return@mapNotNull null
                            val image = Regex("<itunes:image href=\"(.*?)\"").find(item)?.groupValues?.get(1)
                            PodcastEpisode(title = title, description = desc, audioUrl = audio, imageUrl = image)
                        } catch (e: Exception) { null }
                    }
                } catch (e: Exception) { emptyList() }
            }
            if (!isAdded) return@launch
            _binding?.let {
                it.podcastRecyclerView.adapter = PodcastAdapter(episodes) { episode -> playEpisode(episode) }
            }
        }
    }

    private fun playEpisode(episode: PodcastEpisode) {
        if (!isAdded) return
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(episode.audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    if (!isAdded) return@setOnPreparedListener
                    start()
                    updatePlayPauseIcon()
                    _binding?.let {
                        it.playerLayout.currentPodcastTitle.text = episode.title
                        it.playerLayout.currentPodcastDescription.text = episode.description
                        it.playerLayout.root.visibility = View.VISIBLE
                    }
                }
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(context, "Playback failed", Toast.LENGTH_SHORT).show()
                    false
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to play episode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}
