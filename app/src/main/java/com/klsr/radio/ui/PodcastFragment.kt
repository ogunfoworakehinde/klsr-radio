package com.klsr.radio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

class PodcastFragment : SafeFragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var currentEpisode: PodcastEpisode? = null

    override fun onSafeViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPodcastBinding.bind(view)
        binding.podcastHeroImage.setImageResource(R.drawable.hepp)
        binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadPodcasts()
        binding.playerLayout.btnPodcastPlayPause.setOnClickListener {
            currentEpisode?.let { ep ->
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.pause()
                        updatePlayPauseIcon(false)
                    } else {
                        mp.start()
                        updatePlayPauseIcon(true)
                    }
                } ?: playEpisode(ep)
            }
        }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerLayout.btnPodcastPlayPause.setImageResource(icon)
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
            binding.podcastRecyclerView.adapter = PodcastAdapter(episodes) { episode -> playEpisode(episode) }
        }
    }

    private fun playEpisode(episode: PodcastEpisode) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(episode.audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    updatePlayPauseIcon(true)
                    binding.playerLayout.currentPodcastTitle.text = episode.title
                    binding.playerLayout.currentPodcastDescription.text = episode.description
                    binding.playerLayout.root.visibility = View.VISIBLE
                }
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(requireContext(), "Playback failed", Toast.LENGTH_SHORT).show()
                    false
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to play episode", Toast.LENGTH_SHORT).show()
            }
        }
        currentEpisode = episode
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}
