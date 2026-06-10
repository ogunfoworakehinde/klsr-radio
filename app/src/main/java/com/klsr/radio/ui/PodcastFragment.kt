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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PodcastFragment : Fragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPodcastBinding.bind(view)
        binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadPodcasts()
        binding.playerLayout.btnPodcastPlayPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause() else it.start()
                updatePlayPause()
            }
        }
    }

    private fun updatePlayPause() {
        binding.playerLayout.btnPodcastPlayPause.setImageResource(
            if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )
    }

    private fun loadPodcasts() {
        lifecycleScope.launch {
            val episodes = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://anchor.fm/s/1d6ad87c/podcast/rss")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    val text = conn.inputStream.bufferedReader().readText()
                    // simple XML parsing using regex (won't work for complex feeds, but fine for this)
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
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to load podcasts", Toast.LENGTH_SHORT).show()
                    emptyList()
                }
            }
            binding.podcastRecyclerView.adapter = PodcastAdapter(episodes) { playEpisode(it) }
        }
    }

    private fun playEpisode(ep: PodcastEpisode) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(ep.audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    updatePlayPause()
                    binding.playerLayout.currentPodcastTitle.text = ep.title
                    binding.playerLayout.root.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Playback failed", Toast.LENGTH_SHORT).show()
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
