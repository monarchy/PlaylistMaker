package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.ui.adapters.player_behavior.PlaylistBehaviorAdapter
import com.example.playlistmaker.ui.media_library.library.BehaviorState
import com.example.playlistmaker.util.extension.FragmentSnackExtension.showSnackBar
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.click_listenners.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var trackObject: Track? = null
    private lateinit var playlistAdapter: PlaylistBehaviorAdapter
    private var bottomSheetBehavior = BottomSheetBehavior<View>()
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val viewModel: PlayerViewModel by lazy {
        getViewModel {
            parametersOf(
                GsonClient.trackFromJson(
                    arguments?.getString(TRACK_TAG).toString()
                ),requireContext()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        trackObject = null

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        val bottomSheetContainer = binding.tracksSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            viewLifecycleOwner.lifecycleScope.launch {
                val trackToAdd = viewModel.currentTrack.first()
                viewModel.addTrackToPlaylist(trackToAdd, playlist)
            }
        }
        playlistAdapter = PlaylistBehaviorAdapter(requireContext(), onPlaylistClickDebounce)

        binding.recyclerView.adapter = playlistAdapter
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        trackObject = arguments?.getString(TRACK_TAG)?.let { GsonClient.trackFromJson(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiPlayerState.Default -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.button_play))
                    }

                    is UiPlayerState.Paused -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.button_play))
                    }

                    is UiPlayerState.Playing -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.button_pause))
                    }

                    is UiPlayerState.Prepared -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.button_play))
                        binding.time.text = state.progress
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            viewModel.behaviorSheetState.collect { state ->
                behaviorRender(state)
            }
        }

        binding.playerBack.setOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.likeButton.setOnClickListener {
            clickOnLikeButton()
        }

        binding.hiddenBack.setOnClickListener() {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.createPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            parentFragment?.findNavController()
                ?.navigate(R.id.action_playerFragment_to_playlistCreateFragment)
        }

        binding.addButton.setOnClickListener {
            binding.hiddenBack.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {}

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.hiddenBack.visibility = View.VISIBLE
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.getPlaylistList()
                        }

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

    override fun onPause() {
        super.onPause()
        viewModel.activityOnPause()
    }

    private fun showUi(track: Track) {
        binding.songName.text = track.trackName
        binding.executor.text = track.artistName
        binding.durationTime.text =
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis)
        binding.albumName.text = track.collectionName
        binding.releaseYearNumber.text = track.releaseDate.take(4)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country
        Glide.with(binding.cover)
            .load(posterHighQuality(track.artworkUrl100))
            .placeholder(R.drawable.player_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.cover)
    }

    private fun behaviorRender(behaviorState: BehaviorState) {
        when (behaviorState) {
            is BehaviorState.EmptyData -> {
                binding.recyclerView.visibility = View.GONE
            }

            is BehaviorState.PlaylistData -> {
                playlistAdapter.playlists = behaviorState.playlists
                binding.recyclerView.visibility = View.VISIBLE
                playlistAdapter.notifyDataSetChanged()
            }

            is BehaviorState.TrackIsAdded -> {
                binding.addButton.setImageResource(R.drawable.button_add_true)
                binding.hiddenBack.visibility = View.GONE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showSnackBar(binding.root, behaviorState.responseMessage)
            }

            is BehaviorState.TrackIsNotAdded -> {
                binding.hiddenBack.visibility = View.GONE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showSnackBar(binding.root, behaviorState.responseMessage)
            }
        }
    }

    private fun isLiked(boolean: Boolean) {
        binding.likeButton.setImageResource(
            if (boolean) R.drawable.is_liked else R.drawable.not_liked
        )
    }

    private fun clickOnLikeButton() {
        viewModel.favoriteControl()
    }

    private fun posterHighQuality(posterUrl: String): String {
        return posterUrl.replaceAfterLast('/', FORMAT_SIZE)
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN

                else -> parentFragment?.findNavController()?.popBackStack()
            }
        }
    }

    companion object {
        private const val TRACK_TAG = "track"
        private const val FORMAT_SIZE = "512x512bb.jpg"
        fun createArgs(track: String): Bundle = bundleOf(
            TRACK_TAG to track,
        )

        private const val CLICK_DEBOUNCE_DELAY = 100L
    }
}