package com.example.playlistmaker.presentation.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.favorite.FavoriteControlInteractor
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.ui.media_library.library.BehaviorState
import com.example.playlistmaker.ui.player.UiPlayerState
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    initialTrack: Track,
    private var mediaPlayer: PlayerInterractor?,
    private val databaseInteractor: FavoriteControlInteractor,
    private val playlistInteractor: PlaylistInteractor,
    application: Application
) : AndroidViewModel(application) {

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private val _uiState = MutableStateFlow<UiPlayerState>(
        UiPlayerState.Default(
            isPlayButtonEnabled = false,
            progress = "00:00",
            track = initialTrack
        )
    )
    val uiState: StateFlow<UiPlayerState> = _uiState

    private val _behaviorSheetState = MutableStateFlow<BehaviorState>(BehaviorState.EmptyData(null))
    val behaviorSheetState: StateFlow<BehaviorState> get() = _behaviorSheetState

    init {
        mediaPlayer?.preparePlayer(initialTrack.previewUrl)
        combineFlows()
    }

    private fun combineFlows() {
        viewModelScope.launch {
            combine(mediaPlayer!!.mediaPlayerState, _uiState) { playerState, currentState ->
                mapToUiState(playerState, currentState.track)
            }
                .collect { _uiState.value = it }
        }
    }

    private fun mapToUiState(playerState: MediaPlayerState, track: Track): UiPlayerState =
        when (playerState) {
            is MediaPlayerState.Default -> {
                UiPlayerState.Default(
                    isPlayButtonEnabled = playerState.isPlayButtonEnabled,
                    progress = playerState.progress,
                    track = track
                )
            }
            is MediaPlayerState.Paused -> {
                UiPlayerState.Paused(
                    isPlayButtonEnabled = playerState.isPlayButtonEnabled,
                    progress = playerState.progress,
                    track = track
                )
            }
            is MediaPlayerState.Playing -> {
                UiPlayerState.Playing(
                    isPlayButtonEnabled = playerState.isPlayButtonEnabled,
                    progress = playerState.progress,
                    track = track
                )
            }
            is MediaPlayerState.Prepared -> {
                UiPlayerState.Prepared(
                    isPlayButtonEnabled = playerState.isPlayButtonEnabled,
                    progress = playerState.progress,
                    track = track
                )
            }
        }

    fun playBackControl() {
        mediaPlayer?.playBackControl(viewModelScope)
    }

    fun favoriteControl() {
        viewModelScope.launch {
            val currentTrack = _uiState.value.track
            val updatedTrack = currentTrack.copy(isFavorite = !currentTrack.isFavorite) // copy() для Track
            val currentState = _uiState.value
            val newState = when (currentState) {
                is UiPlayerState.Default -> UiPlayerState.Default(
                    isPlayButtonEnabled = currentState.isPlayButtonEnabled,
                    progress = currentState.progress,
                    track = updatedTrack
                )
                is UiPlayerState.Prepared -> UiPlayerState.Prepared(
                    isPlayButtonEnabled = currentState.isPlayButtonEnabled,
                    progress = currentState.progress,
                    track = updatedTrack
                )
                is UiPlayerState.Playing -> UiPlayerState.Playing(
                    isPlayButtonEnabled = currentState.isPlayButtonEnabled,
                    progress = currentState.progress,
                    track = updatedTrack
                )
                is UiPlayerState.Paused -> UiPlayerState.Paused(
                    isPlayButtonEnabled = currentState.isPlayButtonEnabled,
                    progress = currentState.progress,
                    track = updatedTrack
                )
            }
            _uiState.value = newState
            databaseInteractor.favoriteControl(updatedTrack)
        }
    }

    fun activityOnPause() {
        mediaPlayer?.pauseMusic()
    }

    fun getPlaylistList() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                withContext(Dispatchers.Main) {
                    if (playlists.isEmpty()) {
                        _behaviorSheetState.value = BehaviorState.EmptyData(null)
                    } else {
                        _behaviorSheetState.value = BehaviorState.PlaylistData(playlists)
                    }
                }
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(playlist, track).collect { result ->
                when {
                    result.isSuccess -> {
                        val responseString = result.getOrNull() ?: getApplication<Application>().getString(R.string.track_added)
                        _behaviorSheetState.value = BehaviorState.TrackIsAdded(responseString)
                    }
                    result.isFailure -> {
                        val responseString = result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.track_added)
                        _behaviorSheetState.value = BehaviorState.TrackIsNotAdded(responseString)
                    }
                }
            }
        }
    }
}