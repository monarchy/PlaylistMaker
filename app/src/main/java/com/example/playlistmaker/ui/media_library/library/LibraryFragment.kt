package com.example.playlistmaker.ui.media_library.library

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
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.library.LibraryFragmentViewModel
import com.example.playlistmaker.ui.adapters.playlists_library.PlaylistListAdapter
import com.example.playlistmaker.ui.playlist.PlaylistFragment
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.click_listenners.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var playlistAdapter: PlaylistListAdapter
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val viewModel: LibraryFragmentViewModel by viewModel { parametersOf(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
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

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            viewLifecycleOwner.lifecycleScope.launch {
                parentFragment?.findNavController()?.navigate(
                    R.id.action_mediaLibraryFragment_to_playlistFragment,
                    PlaylistFragment.createArgs(GsonClient.playlistToJson(playlist))
                )
            }
        }


        playlistAdapter = PlaylistListAdapter(requireContext(), onPlaylistClickDebounce)
        binding.recyclerView.adapter = playlistAdapter

        binding.createPlaylist.setOnClickListener {
            parentFragment?.findNavController()
                ?.navigate(R.id.action_mediaLibraryFragment_to_playlistCreateFragment)
        }
    }

    private fun render(favoriteState: PlaylistLibraryState) {
        when (favoriteState) {
            is PlaylistLibraryState.Content -> {
                binding.placeholder.visibility = View.GONE
                binding.emptyText.visibility = View.GONE
                playlistAdapter.playlists = favoriteState.playlistList
                playlistAdapter.notifyDataSetChanged()
                binding.recyclerView.visibility = View.VISIBLE
            }

            PlaylistLibraryState.Empty -> {
                binding.placeholder.visibility = View.VISIBLE
                binding.emptyText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance() = LibraryFragment().apply {
            arguments = bundleOf()
        }

        private const val CLICK_DEBOUNCE_DELAY = 100L
    }

}