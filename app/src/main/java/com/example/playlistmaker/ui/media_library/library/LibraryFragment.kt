package com.example.playlistmaker.ui.media_library.library

import android.os.Bundle
import android.util.Log
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
import com.example.playlistmaker.presentation.library.LibraryFragmentViewModel
import com.example.playlistmaker.ui.common.playlistList.PlaylistListAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var playlistAdapter: PlaylistListAdapter
    private val viewModel: LibraryFragmentViewModel by viewModel()
    private val TAG = "LibraryFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect() { state ->
                    Log.d(TAG, "Rendering state: $state")
                    render(state)
                }
            }
        }

        playlistAdapter = PlaylistListAdapter(requireContext()) { playlist ->
            Log.d(TAG, "Clicked playlist: ${playlist.playlistName}, ID: ${playlist.playlistId}")
            val bottomSheet = PlaylistTrackBottomSheetFragment.newInstance(playlist.playlistId)
            bottomSheet.show(requireActivity().supportFragmentManager, "PlaylistTrackBottomSheet")
        }
        binding.createPlaylist.setOnClickListener {
            parentFragment?.findNavController()
                ?.navigate(R.id.action_mediaLibraryFragment_to_playlistCreateFragment)
        }
        binding.recyclerView.adapter = playlistAdapter
        Log.d(TAG, "RecyclerView adapter set")
    }

    private fun render(favoriteState: PlaylistLibraryState) {
        when (favoriteState) {
            is PlaylistLibraryState.Content -> {
                binding.placeholder.visibility = View.GONE
                binding.emptyText.visibility = View.GONE
                playlistAdapter.playlists = favoriteState.playlistList
                playlistAdapter.notifyDataSetChanged()
                binding.recyclerView.visibility = View.VISIBLE
                Log.d(TAG, "Rendered Content with ${favoriteState.playlistList.size} playlists")
            }
            PlaylistLibraryState.Empty -> {
                binding.placeholder.visibility = View.VISIBLE
                binding.emptyText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                Log.d(TAG, "Rendered Empty state")
            }
        }
    }

    companion object {
        fun newInstance() = LibraryFragment().apply {
            arguments = bundleOf()
        }
    }
}