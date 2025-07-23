package com.example.playlistmaker.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.ui.playlist.PlaylistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlist: Playlist,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val _playlistState = MutableStateFlow<PlaylistState>(PlaylistState.OnUpdate(playlist))
    val playlistState: StateFlow<PlaylistState> = _playlistState.asStateFlow()

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylistWithTracks(playlist.playlistId)
                .collect { actualPlaylist ->
                    when(actualPlaylist){
                        null -> _playlistState.value = PlaylistState.IsDeleted
                        else -> postData(actualPlaylist)
                    }
                }
        }
    }

    fun deleteTrack(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }


    suspend fun sharePlaylist(playlistId: Long): String? {
        return playlistInteractor.sharePlaylist(playlistId)
            .firstOrNull()
            ?.exceptionOrNull()
            ?.message
    }

    private fun checkPlaylistTitle(playlist: Playlist): String {
        return when (playlist.playlistTitle.isNullOrEmpty()) {
            true -> "Описание отсутствует."
            false -> playlist.playlistTitle
        }
    }

    private fun postData(playlistWithTracks: PlaylistWithTracks) {
        val playlistFormated =
            playlistWithTracks.playlistInfo.copy(
                playlistTitle = checkPlaylistTitle(
                    playlistWithTracks.playlistInfo
                )
            )
        _playlistState.value =
            PlaylistState.Data(playlistFormated, playlistWithTracks.trackList)
    }

}