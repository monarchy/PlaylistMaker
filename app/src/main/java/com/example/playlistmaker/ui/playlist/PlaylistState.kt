package com.example.playlistmaker.ui.playlist

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

sealed interface PlaylistState {
    data class OnUpdate(val playlist: Playlist):PlaylistState
    data class Data(val playlist: Playlist, val trackList: List<Track>): PlaylistState
    data object IsDeleted:PlaylistState
}