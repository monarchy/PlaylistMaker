package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val _mediaPlayerState = MutableLiveData<MediaPlayerState>(MediaPlayerState.Default())
    override val mediaPlayerState: LiveData<MediaPlayerState> get() = _mediaPlayerState

    override fun preparePlayer(trackPreviewUrl: String) {
        trackUrl = trackPreviewUrl
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _mediaPlayerState.postValue(MediaPlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            _mediaPlayerState.postValue(MediaPlayerState.Default())
            mediaPlayer.reset()
            mediaPlayer.setDataSource(trackPreviewUrl)
            mediaPlayer.prepareAsync()
            _mediaPlayerState.postValue(MediaPlayerState.Prepared())
        }
    }

    override fun playMusic(scope: CoroutineScope) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _mediaPlayerState.postValue(MediaPlayerState.Playing(getPlayTimer()))
            startTimer(scope)
        }
    }

    override fun startTimer(scope: CoroutineScope) {
        timeJob?.cancel()
        timeJob = scope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME)
                _mediaPlayerState.postValue(MediaPlayerState.Playing(getPlayTimer()))
            }
        }
    }

    override fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            timeJob?.cancel()
            _mediaPlayerState.postValue(MediaPlayerState.Paused(getPlayTimer()))
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
                trackUrl?.let {preparePlayer(trackUrl!!)}
            }
        }
    }

    override fun getPlayTimer(): String {
        return dateFormat.format(mediaPlayer.duration - mediaPlayer.currentPosition)
    }

    companion object{
        private const val UPDATE_TIME:Long = 300
    }
}