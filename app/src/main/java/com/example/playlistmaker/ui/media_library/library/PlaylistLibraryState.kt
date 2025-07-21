package com.example.playlistmaker.ui.media_library.library

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistLibraryState {
    data object Empty : PlaylistLibraryState
    data class Content(val playlistList: List<Playlist>) : PlaylistLibraryState
}