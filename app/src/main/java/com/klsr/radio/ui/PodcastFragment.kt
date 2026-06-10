package com.klsr.radio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
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
import java.net.URL

class PodcastFragment : Fragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var episodes = emptyList<PodcastEpisode>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPodcastBinding.bind(view)

        binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadPodcasts()

        binding.playerLayout.btnPodcastPlayPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause() else it.start()
                updatePlayPauseButton()
            }
        }
    }

    private fun updatePlayPauseButton() {
        val icon = if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
        binding.playerLayout.btnPodcastPlayPause.setImageResource(icon)
    }

    private fun loadPodcasts() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://anchor.fm/s/1d6ad87c/podcast/rss")
                    val parser = XmlPullParserFactory.newInstance().newPullParser()
                    parser.setInput(url.openStream(), null)
                    var eventType = parser.eventType
                    val episodes = mutableListOf<PodcastEpisode>()
                    var currentEpisode: PodcastEpisode? = null
                    var currentTag = ""
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        when (eventType) {
                            XmlPullParser.START_TAG -> {
                                currentTag = parser.name
                                if (currentTag == "item") currentEpisode = PodcastEpisode()
                            }
                            XmlPullParser.TEXT -> {
                                when (currentTag) {
                                    "title" -> currentEpisode?.title = parser.text
                                    "description" -> currentEpisode?.description = parser.text
                                    "itunes:image" -> currentEpisode?.imageUrl = parser.getAttributeValue(null, "href")
                                    "enclosure" -> currentEpisode?.audioUrl = parser.getAttributeValue(null, "url")
                                    "pubDate" -> currentEpisode?.pubDate = parser.text
                                    "itunes:duration" -> currentEpisode?.duration = parser.text
                                }
                            }
                            XmlPullParser.END_TAG -> {
                                if (parser.name == "item") {
                                    currentEpisode?.let { episodes.add(it) }
                                    currentEpisode = null
                                }
                                currentTag = ""
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
            binding.podcastRecyclerView.adapter = PodcastAdapter(list) { episode ->
                playEpisode(episode)
            }
            binding.paginationLayout.visibility = View.GONE
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
                    updatePlayPauseButton()
                    binding.playerLayout.currentPodcastTitle.text = episode.title
                    binding.playerLayout.currentPodcastInfo.text = episode.description
                    binding.playerLayout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
