package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.util.MediaPlayerState

class PlayerViewModel(
    trackPreviewUrl: String,
    private var mediaPlayer: PlayerInterractor?,
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }

    private val _playerStateUi = MutableLiveData<MediaPlayerState>()
    val playerStateUi: LiveData<MediaPlayerState> get() = _playerStateUi

    init {
        mediaPlayer?.preparePlayer(trackPreviewUrl)
        mediaPlayer?.mediaPlayerState?.observeForever { state ->
            _playerStateUi.postValue(state)
        }
    }

    fun playBackControl() {
        mediaPlayer?.playBackControl(viewModelScope)
    }


    fun activityOnPause() {
        mediaPlayer?.pauseMusic()
    }
}