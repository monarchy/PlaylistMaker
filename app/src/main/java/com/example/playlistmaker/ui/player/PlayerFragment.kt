package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.MediaPlayerState
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
                GsonClient.fromJsonToPlayer(
                    arguments?.getString(TRACK_TAG).toString()
                ).previewUrl
            )
        }
    }

    override fun onDestroy() {
        findNavController().popBackStack(R.id.searchFragment, false)
        _binding = null
        super.onDestroy()
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
        trackObject = arguments?.getString(TRACK_TAG)?.let { GsonClient.fromJsonToPlayer(it) }
        viewModel.playerStateUi.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MediaPlayerState.Default -> {
                    showUi(trackObject!!)
                    binding.time.text = state.progress
                    binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                    binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                }

                is MediaPlayerState.Paused -> {
                    showUi(trackObject!!)
                    binding.time.text = state.progress
                    binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                    binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                }

                is MediaPlayerState.Playing -> {
                    showUi(trackObject!!)
                    binding.time.text = state.progress
                    binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                    binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.pause))
                }

                is MediaPlayerState.Prepared -> {
                    showUi(trackObject!!)
                    binding.playPauseButton.isEnabled = state.isPlayButtonEnabled
                    binding.playPauseButton.setImageDrawable(requireContext().getDrawable(R.drawable.play_button))
                    binding.time.text = state.progress
                }
            }
        }

        binding.playerBack.setOnClickListener {
            onDestroy()
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playBackControl()
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
            .load(trackObject?.let { trackObject?.artworkUrl100 })
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.cover)
    }

    companion object {
        private const val TRACK_TAG = "track"
        fun createArgs(track: String): Bundle = bundleOf(
            TRACK_TAG to track
        )
    }
}