package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.klsr.radio.R
import com.klsr.radio.adapters.PodcastAdapter
import com.klsr.radio.databinding.FragmentPodcastBinding

class PodcastFragment : Fragment(R.layout.fragment_podcast) {
    private var _binding: FragmentPodcastBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPodcastBinding.bind(view)

        val episodes = listOf("Episode 1", "Episode 2", "Episode 3")
        binding.podcastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.podcastRecyclerView.adapter = PodcastAdapter(episodes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
