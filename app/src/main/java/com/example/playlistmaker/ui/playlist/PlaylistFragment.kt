package com.example.playlistmaker.ui.playlist

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.adapters.playlist_fragment.PlaylistTracksAdapter
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.ui.playlist_forms.playlist_edit_form.PlaylistEditFragment
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.click_listenners.debounce
import com.example.playlistmaker.util.click_listenners.longTrackClick
import com.example.playlistmaker.util.extension.FragmentSnackExtension.showSnackBar
import com.example.playlistmaker.util.extension.minutesToString
import com.example.playlistmaker.util.extension.toMinutes
import com.example.playlistmaker.util.extension.trackCountToString
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksAdapter: PlaylistTracksAdapter
    private var trackListBehavior = BottomSheetBehavior<View>()
    private var settingPlaylistBehavior = BottomSheetBehavior<View>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onTrackLongClick: (Track) -> Unit
    private lateinit var playlist: Playlist
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playlistJson = arguments?.getString(PLAYLIST_ID_TAG).toString()
        playlist = GsonClient.playlistFromJson(playlistJson)
        viewModel = getViewModel { parametersOf(playlist, requireContext()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        val trackListContainer = binding.tracksSheetBehavior
        val playlistDetailContainer = binding.playlistEditBehavior
        trackListBehavior = BottomSheetBehavior.from(trackListContainer)
        settingPlaylistBehavior = BottomSheetBehavior.from(playlistDetailContainer)
        settingPlaylistBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistState.collect { state ->
                    when (state) {
                        is PlaylistState.Data -> playlist = state.playlist
                        is PlaylistState.OnUpdate -> playlist = state.playlist
                        else -> Unit
                    }
                    renderUi(state)
                }
            }
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(GsonClient.trackToJson(track))
            )
        }

        onTrackLongClick = longTrackClick<Track>(
            viewLifecycleOwner.lifecycleScope
        ) { track ->
            viewLifecycleOwner.lifecycleScope.launch {
                showDeleteDialog(
                    getString(R.string.deleteTrackDialogTitle),
                    null,
                    onPositiveClick = { viewModel.deleteTrack(playlist.playlistId, track.trackId) })
            }
        }

        tracksAdapter = PlaylistTracksAdapter(onTrackClickDebounce, onTrackLongClick)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = tracksAdapter

        binding.shareButton.setOnClickListener {
            sharePlaylist(playlist.playlistId)
        }

        binding.materialShareButton.setOnClickListener {
            settingPlaylistBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist(playlist.playlistId)
        }

        binding.kebabButton.setOnClickListener {
            settingPlaylistBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.deletePlaylist.setOnClickListener {
            showDeleteDialog(
                getString(R.string.delete_playlist),
                getString(R.string.dialog_playlist_delete_confirmation),
                onPositiveClick = { viewModel.deletePlaylist() })
        }

        binding.edit.setOnClickListener {
            parentFragment?.findNavController()?.navigate(
                R.id.action_playlistFragment_to_playlistEditFragment,
                PlaylistEditFragment.createArgs(GsonClient.playlistToJson(playlist))
            )
        }

        binding.hiddenBack.setOnClickListener() {
            settingPlaylistBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistBack.setOnClickListener {
            parentFragment?.findNavController()?.popBackStack()
        }

        settingPlaylistBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {}

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.hiddenBack.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.hiddenBack.visibility = View.GONE
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }

    private fun renderUi(state: PlaylistState) {
        viewLifecycleOwner.lifecycleScope.launch {
            when (state) {
                is PlaylistState.OnUpdate -> {
                    updatePlaylistInfo(state.playlist)
                }

                is PlaylistState.Data -> {
                    updatePlaylistInfo(state.playlist)
                    updateTrackListInfo(state.trackList)
                }

                PlaylistState.IsDeleted -> {
                    Log.d("ASD", "DATA DELETED")
                    parentFragment?.findNavController()?.navigateUp()
                }
            }
        }
    }

    private fun updatePlaylistInfo(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName
        binding.playlistInfoBehavior.playlistBehaviorName.text = playlist.playlistName
        binding.description.text = playlist.playlistTitle
        binding.tracksCount.text = playlist.tracksCount.trackCountToString(requireContext())
        binding.playlistInfoBehavior.playlistTracksCount.text =
            playlist.tracksCount.trackCountToString(requireContext())
        Glide.with(binding.cover)
            .load(playlist.imagePath?.toUri())
            .centerCrop()
            .placeholder(R.drawable.placeholder_for_playlist)
            .error(R.drawable.placeholder_for_playlist)
            .into(binding.cover)
        Glide.with(binding.playlistInfoBehavior.cover)
            .load(playlist.imagePath?.toUri())
            .centerCrop()
            .placeholder(R.drawable.placeholder_for_playlist)
            .error(R.drawable.placeholder_for_playlist)
            .into(binding.playlistInfoBehavior.cover)
    }

    private fun updateTrackListInfo(trackList: List<Track>) {
        binding.totalPlaybackTime.text =
            trackList.sumOf { it.trackTimeMillis }.toMinutes().minutesToString(requireContext())
        when (trackList.isNotEmpty()) {
            true -> {
                binding.emptyTrackList.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                tracksAdapter.tracks = trackList
                tracksAdapter.notifyDataSetChanged()
            }

            false -> {
                binding.emptyTrackList.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    private fun sharePlaylist(playlistId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharePlaylist(playlistId)?.let { errorMessage ->
                showSnackBar(binding.root, errorMessage)
            }
        }
    }

    private fun showDeleteDialog(
        titleString: String,
        messageString: String?,
        onPositiveClick: () -> Unit
    ) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(titleString)
            .apply {
                if (messageString != null) setMessage(messageString)
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                onPositiveClick.invoke()
            }
            .setNegativeButton(getString(R.string.no), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.blue))
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
        }
        dialog.show()

            dialog.show()
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (settingPlaylistBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED -> settingPlaylistBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN

                else -> parentFragment?.findNavController()?.popBackStack()
            }
        }
    }


    companion object {
        fun createArgs(playlist: String): Bundle = bundleOf(
            PLAYLIST_ID_TAG to playlist,
        )

        private const val PLAYLIST_ID_TAG = "idPlaylist"
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}