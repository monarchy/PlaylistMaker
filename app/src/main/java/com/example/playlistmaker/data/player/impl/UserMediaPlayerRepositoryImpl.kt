package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class UserMediaPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) :
    UserMediaPlayerRepository {
    private var timeJob: Job? = null
    private var trackUrl: String? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val _mediaPlayerState = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Default())
    override val mediaPlayerState: StateFlow<MediaPlayerState> = _mediaPlayerState.asStateFlow()

    override fun preparePlayer(trackPreviewUrl: String) {
        trackUrl = trackPreviewUrl
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _mediaPlayerState.value = MediaPlayerState.Prepared()
        }
        mediaPlayer.setOnCompletionListener {
            _mediaPlayerState.value = MediaPlayerState.Default()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(trackPreviewUrl)
            mediaPlayer.prepareAsync()
            _mediaPlayerState.value = MediaPlayerState.Prepared()
        }
    }

    override fun playMusic(scope: CoroutineScope) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _mediaPlayerState.value = MediaPlayerState.Playing(getPlayTimer())
            startTimer(scope)
        }
    }

    override fun startTimer(scope: CoroutineScope) {
        timeJob?.cancel()
        timeJob = scope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME)
                _mediaPlayerState.value = MediaPlayerState.Playing(getPlayTimer())
            }
        }
    }

    override fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            timeJob?.cancel()
            _mediaPlayerState.value = MediaPlayerState.Paused(getPlayTimer())
        }
    }

    override fun release() {
        timeJob?.cancel()
        timeJob = null
        mediaPlayer.release()
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun playbackControl(scope: CoroutineScope) {
        when (mediaPlayerState.value) {
            is MediaPlayerState.Playing -> {
                pauseMusic()
            }

            is MediaPlayerState.Prepared, is MediaPlayerState.Paused -> {
                playMusic(scope)
            }

            is MediaPlayerState.Default -> {
                if (trackUrl?.isEmpty() != true)
                    preparePlayer(trackUrl!!)
            }

            null -> {
                trackUrl?.let { preparePlayer(trackUrl!!) }
            }
        }
    }

    override fun getPlayTimer(): String {
        return dateFormat.format(mediaPlayer.duration - mediaPlayer.currentPosition)
    }

    companion object {
        private const val UPDATE_TIME: Long = 300
    }
}