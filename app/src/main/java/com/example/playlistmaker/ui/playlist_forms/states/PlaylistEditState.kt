package com.example.playlistmaker.ui.playlist_forms.states

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistEditState: PlaylistCreateState {
    data class OnEditing(val playlist: Playlist) : PlaylistEditState
    data object Success : PlaylistEditState
}