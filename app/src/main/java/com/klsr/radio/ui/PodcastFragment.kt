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
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL

class PodcastFragment : Fragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var episodes = emptyList<PodcastEpisode>()
    private var currentEpisode: PodcastEpisode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPodcastBinding.bind(view)

        binding.podcastHeroImage.setImageResource(R.drawable.hepp)
        binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadPodcasts()

        binding.playerLayout.btnPodcastPlayPause.setOnClickListener {
            currentEpisode?.let { ep ->
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
                updatePlayPauseIcon()
            }
        }
    }

    private fun updatePlayPauseIcon() {
        val icon = if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerLayout.btnPodcastPlayPause.setImageResource(icon)
    }

    private fun loadPodcasts() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://anchor.fm/s/1d6ad87c/podcast/rss")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    val input = conn.inputStream
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    val parser = factory.newPullParser()
                    parser.setInput(input, null)
                    var eventType = parser.eventType
                    val episodes = mutableListOf<PodcastEpisode>()
                    var current: PodcastEpisode? = null
                    var tag = ""
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        when (eventType) {
                            XmlPullParser.START_TAG -> {
                                tag = parser.name
                                if (tag == "item") {
                                    current = PodcastEpisode()
                                }
                            }
                            XmlPullParser.TEXT -> {
                                val text = parser.text ?: ""
                                when (tag) {
                                    "title" -> current?.title = text
                                    "description" -> current?.description = text
                                    "itunes:image" -> current?.imageUrl = parser.getAttributeValue(null, "href")
                                    "enclosure" -> current?.audioUrl = parser.getAttributeValue(null, "url")
                                    "pubDate" -> current?.pubDate = text
                                    "itunes:duration" -> current?.duration = text
                                    "itunes:author" -> current?.author = text
                                }
                            }
                            XmlPullParser.END_TAG -> {
                                if (parser.name == "item") {
                                    current?.let { episodes.add(it) }
                                    current = null
                                }
                                tag = ""
                            }
                        }
                        eventType = parser.next()
                    }
                    episodes
                } catch (e: Exception) {
                    emptyList()
                }
            }
            episodes = list
            binding.episodeCountText.text = "${list.size} episodes available"
            binding.podcastRecyclerView.adapter = PodcastAdapter(list) { episode -> playEpisode(episode) }
        }
    }

    private fun playEpisode(episode: PodcastEpisode) {
        currentEpisode = episode
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(episode.audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    updatePlayPauseIcon()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}
