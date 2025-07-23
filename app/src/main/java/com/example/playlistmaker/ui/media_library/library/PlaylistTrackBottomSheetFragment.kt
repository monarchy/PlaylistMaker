package com.example.playlistmaker.ui.media_library.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentBottomSheetBinding
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.presentation.library.PlaylistTracksViewModel
import com.example.playlistmaker.ui.common.trackList.TrackListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistTrackBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistTracksViewModel by viewModel()
    private lateinit var adapter: TrackListAdapter
    private val TAG = "PlaylistTrackBottomSheet"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView called for playlistId: ${arguments?.getLong("playlistId")}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = requireArguments().getLong("playlistId")
        Log.d(TAG, "onViewCreated called for playlistId: $playlistId")
        adapter = TrackListAdapter { track ->
            Log.d(TAG, "Track clicked: ${track.trackName}")
        }
        binding.recyclerViewTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTracks.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTracksForPlaylist(playlistId).collectLatest { playlistWithTracks: PlaylistWithTracks? ->
                    Log.d(TAG, "Collecting data: $playlistWithTracks")
                    playlistWithTracks?.let { safePlaylistWithTracks ->
                        Log.d(TAG, "Received playlist: ${safePlaylistWithTracks.playlistInfo.playlistName}")
                        adapter.tracks = safePlaylistWithTracks.trackList
                        adapter.notifyDataSetChanged()
                        binding.playlistTitle.text = safePlaylistWithTracks.playlistInfo.playlistName
                    } ?: run {
                        Log.d(TAG, "No playlist data received")
                        binding.playlistTitle.text = "Нет данных"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView called")
    }

    companion object {
        fun newInstance(playlistId: Long): PlaylistTrackBottomSheetFragment {
            return PlaylistTrackBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putLong("playlistId", playlistId)
                }
            }
        }
    }
}