package com.example.playlistmaker.presentation.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.DatabaseInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.util.MediaPlayerState
import com.example.playlistmaker.ui.player.UiPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerViewModel(
    track: Track,
    private var mediaPlayer: PlayerInterractor?,
    private val databaseInteractor: DatabaseInteractor,
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    init {
        mediaPlayer?.preparePlayer(track.previewUrl)
        combineFlows()
    }

    private val _favoriteState = MutableStateFlow(track.isFavorite)

    private val _uiState = MutableStateFlow<UiPlayerState>(
        UiPlayerState.Default(false, "00:00", track.isFavorite)
    )
    val uiState: StateFlow<UiPlayerState> = _uiState

    private val _currentTrack = MutableStateFlow(track)

    private fun combineFlows() {
        viewModelScope.launch {
            mediaPlayer!!.mediaPlayerState.combine(_favoriteState) { playerState, isFavorite ->
                mapToUiState(playerState, isFavorite)
            }
                .collect { _uiState.value = it }
        }
    }

    private fun mapToUiState(playerState: MediaPlayerState, isFavorite: Boolean): UiPlayerState =
        when (playerState) {
            is MediaPlayerState.Default -> {
                UiPlayerState.Default(
                    playerState.isPlayButtonEnabled,
                    playerState.progress,
                    isFavorite
                )
            }

            is MediaPlayerState.Paused -> {
                UiPlayerState.Paused(
                    playerState.isPlayButtonEnabled,
                    playerState.progress,
                    isFavorite
                )
            }

            is MediaPlayerState.Playing -> {
                UiPlayerState.Playing(
                    playerState.isPlayButtonEnabled,
                    playerState.progress,
                    isFavorite
                )
            }

            is MediaPlayerState.Prepared -> {
                UiPlayerState.Prepared(
                    playerState.isPlayButtonEnabled,
                    playerState.progress,
                    isFavorite
                )
            }
        }

    fun playBackControl() {
        mediaPlayer?.playBackControl(viewModelScope)
    }

    fun favoriteControl() {
        viewModelScope.launch {
            val newState = !_currentTrack.value.isFavorite
            _currentTrack.value = _currentTrack.value.copy(isFavorite = newState)

            if (newState) {
                databaseInteractor.addToFavorite(_currentTrack.value)
                _favoriteState.value = _currentTrack.value.isFavorite
            } else {
                databaseInteractor.removeFromFavorite(_currentTrack.value.trackId)
                _favoriteState.value = _currentTrack.value.isFavorite
            }
        }
    }

    fun activityOnPause() {
        mediaPlayer?.pauseMusic()
    }
}
