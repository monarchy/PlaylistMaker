package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerState
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel by viewModel<AudioPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>(Constants.TRACK)
            ?: throw IllegalArgumentException("Track data is missing")

        setupTrackInfo(track)
        viewModel.setDataSource(track.previewUrl)

        viewModel.playerState.observe(this, ::updateUI)

        binding.buttonPlay.setOnClickListener { viewModel.playbackControl() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setupTrackInfo(track: Track) = with(binding) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        collectionName.apply {
            text = track.collectionName
            visibility = if (track.collectionName.isEmpty()) View.GONE else View.VISIBLE
        }

        releaseDate.text = track.releaseDate.take(4)  // Берем только год
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(imageMusic.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(8.dpToPx()))
            .into(imageMusic)
    }

    private fun updateUI(state: AudioPlayerState) = with(binding) {
        buttonPlay.isEnabled = when (state) {
            is AudioPlayerState.Default -> state.isPlayButtonEnabled
            is AudioPlayerState.Prepared -> state.isPlayButtonEnabled
            is AudioPlayerState.Playing -> state.isPlayButtonEnabled
            is AudioPlayerState.Paused -> state.isPlayButtonEnabled
        }

        when (state) {
            is AudioPlayerState.Prepared -> {
                buttonPlay.setImageResource(R.drawable.play_button)
                timePlay.setText(R.string.timer)
            }
            is AudioPlayerState.Playing -> {
                buttonPlay.setImageResource(R.drawable.pause)
                timePlay.text = state.currentPosition
            }
            is AudioPlayerState.Paused -> {
                buttonPlay.setImageResource(R.drawable.play_button)
                timePlay.text = state.currentPosition
            }
            else -> {}
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
