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
    private val binding get() = _binding
    private var mediaPlayer: MediaPlayer? = null
    private var episodes = emptyList<PodcastEpisode>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPodcastBinding.bind(view)

        binding?.apply {
            podcastHeroImage.setImageResource(R.drawable.hepp)
            podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            podcastRecyclerView.isNestedScrollingEnabled = false // handled by NestedScrollView

            loadPodcasts()

            playerLayout.btnPodcastPlayPause.setOnClickListener {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.pause() else mp.start()
                    updatePlayPauseIcon()
                }
            }
        }
    }

    private fun updatePlayPauseIcon() {
        binding?.playerLayout?.btnPodcastPlayPause?.setImageResource(
            if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )
    }

    private fun loadPodcasts() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
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
                            val pubDate = Regex("<pubDate>(.*?)</pubDate>").find(item)?.groupValues?.get(1)
                            PodcastEpisode(title = title, description = desc, audioUrl = audio, imageUrl = image, pubDate = pubDate)
                        } catch (e: Exception) { null }
                    }
                } catch (e: Exception) { emptyList() }
            }

            episodes = list
            binding?.apply {
                episodeCountText.text = "${list.size} episodes available"
                podcastRecyclerView.adapter = PodcastAdapter(list) { episode -> playEpisode(episode) }
            }
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
                    updatePlayPauseIcon()
                    binding?.playerLayout?.apply {
                        currentPodcastTitle.text = episode.title
                        currentPodcastDescription.text = episode.description
                        root.visibility = View.VISIBLE
                    }
                }
                setOnErrorListener { _, _, _ -> false }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to play", Toast.LENGTH_SHORT).show()
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
