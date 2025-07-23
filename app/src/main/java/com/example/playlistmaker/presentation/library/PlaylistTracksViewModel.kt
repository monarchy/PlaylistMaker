package com.example.playlistmaker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistTracksViewModel : ViewModel(), KoinComponent {
    private val playlistInteractor: PlaylistInteractor by inject()

    private val _playlistWithTracks = MutableStateFlow<PlaylistWithTracks?>(null)
    val playlistWithTracks: StateFlow<PlaylistWithTracks?> = _playlistWithTracks

    fun getTracksForPlaylist(playlistId: Long): Flow<PlaylistWithTracks?> {
        viewModelScope.launch {
            playlistInteractor.getPlaylistWithTracks(playlistId).collect { playlistWithTracks ->
                _playlistWithTracks.value = playlistWithTracks
            }
        }
        return _playlistWithTracks // Возвращаем StateFlow как Flow
    }
}