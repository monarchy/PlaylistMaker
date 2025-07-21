package com.example.playlistmaker.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    track: Track,
    private var mediaPlayer: PlayerInterractor?,
    private val databaseInteractor: FavoriteControlInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private val _currentTrack = MutableStateFlow<Track>(track)
    val currentTrack: StateFlow<Track> get() = _currentTrack
    private val _uiState = MutableStateFlow<UiPlayerState>(
        UiPlayerState.Default(false, "00:00", track.isFavorite)
    )
    val uiState: StateFlow<UiPlayerState> = _uiState

    private val _behaviorSheetState = MutableStateFlow<BehaviorState>(BehaviorState.EmptyData(null))
    val behaviorSheetState: StateFlow<BehaviorState> get() = _behaviorSheetState

    init {
        mediaPlayer?.preparePlayer(track.previewUrl)
        combineFlows()
    }


    private fun combineFlows() {
        viewModelScope.launch {
            combine(mediaPlayer!!.mediaPlayerState, _currentTrack) { playerState, currentTrack ->
                mapToUiState(playerState, currentTrack.isFavorite)
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
            _currentTrack.value =
                _currentTrack.value.copy(isFavorite = !_currentTrack.value.isFavorite)
            databaseInteractor.favoriteControl(_currentTrack.value)
        }
    }

    fun activityOnPause() {
        mediaPlayer?.pauseMusic()
    }

    fun getPlaylistList() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getAllPlaylists().collect() { playlists ->
                withContext(Dispatchers.Main) {
                    if (playlists.isEmpty()) {
                        _behaviorSheetState.value = BehaviorState.EmptyData(null)
                    } else _behaviorSheetState.value = BehaviorState.PlaylistData(playlists)
                }
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(playlist, track).collect { result ->
                when {
                    result.isSuccess -> {
                        val responseString = result.getOrNull() ?: "Трек добавлен"
                        _behaviorSheetState.value = BehaviorState.TrackIsAdded(responseString)
                    }

                    result.isFailure -> {
                        val responseString = result.exceptionOrNull()?.message ?: "Трек добавлен"
                        _behaviorSheetState.value = BehaviorState.TrackIsNotAdded(responseString)
                    }
                }
            }
        }
    }
}
