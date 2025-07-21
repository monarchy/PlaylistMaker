package com.example.playlistmaker.ui.media_library.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.FavoriteTracksViewModel
import com.example.playlistmaker.ui.common.trackList.TrackListAdapter
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackListAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect() { state ->
                    render(state)
                }
            }
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            parentFragment?.findNavController()?.navigate(
                R.id.action_mediaLibraryFragment_to_playerFragment,
                PlayerFragment.createArgs(GsonClient.objectToJson(track))
            )
        }
        trackAdapter = TrackListAdapter(onTrackClickDebounce)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter
    }

    private fun render(favoriteState: FavoriteState) {
        when (favoriteState) {
            is FavoriteState.Content -> {
                binding.placeholder.visibility = View.GONE
                binding.emptyText.visibility = View.GONE
                trackAdapter.tracks = favoriteState.favoriteList
                trackAdapter.notifyDataSetChanged()
                binding.recyclerView.visibility = View.VISIBLE
            }

            FavoriteState.Empty -> {
                binding.placeholder.visibility = View.VISIBLE
                binding.emptyText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment().apply {
            arguments = bundleOf()
        }

        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

}