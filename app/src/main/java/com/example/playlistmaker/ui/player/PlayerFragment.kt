package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.util.GsonClient
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var trackObject: Track? = null

    private val viewModel: PlayerViewModel by lazy {
        getViewModel {
            parametersOf(
                GsonClient.objectFromJson(
                    arguments?.getString(TRACK_TAG).toString()
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        trackObject = arguments?.getString(TRACK_TAG)?.let { GsonClient.objectFromJson(it) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect() { state ->
                when (state) {
                    is UiPlayerState.Default -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                    }

                    is UiPlayerState.Paused -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                    }

                    is UiPlayerState.Playing -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.time.text = state.progress
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.pause))
                    }

                    is UiPlayerState.Prepared -> {
                        showUi(trackObject!!)
                        isLiked(state.isFavorite)
                        binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                        binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                        binding.time.text = state.progress
                    }
                }
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
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.cover)
    }

    private fun isLiked(boolean: Boolean) {
        binding.likeButton.setImageResource(
            if (boolean) R.drawable.like else R.drawable.not_like
        )
    }

    private fun clickOnLikeButton() {
        viewModel.favoriteControl()
    }

    private fun posterHighQuality(posterUrl: String): String {
        return posterUrl.replaceAfterLast('/', FORMAT_SIZE)
    }

    companion object {
        private const val TRACK_TAG = "track"
        private const val FORMAT_SIZE = "512x512bb.jpg"
        fun createArgs(track: String): Bundle = bundleOf(
            TRACK_TAG to track,
        )
    }
}