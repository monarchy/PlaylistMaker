package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel : ViewModel() {

    companion object {
        const val UPDATE_TIME = 300L
    }

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> get() = _playerState

    private var previewUrl: String? = null

    init {
        _playerState.value = AudioPlayerState.Default()
    }

    fun setDataSource(previewUrl: String) {
        this.previewUrl = previewUrl
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.value = AudioPlayerState.Prepared()
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.value = AudioPlayerState.Prepared()
            handler.removeCallbacks(updatingTime)
        }
    }

    private val updatingTime = object : Runnable {
        override fun run() {
            val currentState = _playerState.value
            if (currentState is AudioPlayerState.Playing) {
                updateState(currentState.copy(currentPosition = formatTime(mediaPlayer.currentPosition)))
                handler.postDelayed(this, UPDATE_TIME)
            }
        }
    }

    fun startPlayer() {
        mediaPlayer.start()
        updateState(AudioPlayerState.Playing(currentPosition = formatTime(mediaPlayer.currentPosition)))
        handler.post(updatingTime)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        updateState(AudioPlayerState.Paused(currentPosition = formatTime(mediaPlayer.currentPosition)))
        handler.removeCallbacks(updatingTime)
    }

    fun playbackControl() {
        when (_playerState.value) {
            is AudioPlayerState.Playing -> pausePlayer()
            is AudioPlayerState.Prepared, is AudioPlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updatingTime)
    }

    // Вспомогательные методы
    private fun formatTime(milliseconds: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }

    private fun updateState(newState: AudioPlayerState) {
        _playerState.value = newState
    }
}