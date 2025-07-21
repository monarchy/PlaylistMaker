package com.example.playlistmaker.ui.media_library.library

import com.example.playlistmaker.domain.models.Playlist

sealed class BehaviorState {
    data class EmptyData(val playlists: Any?) : BehaviorState()
    data class PlaylistData(val playlists: List<Playlist>) : BehaviorState()
    data class TrackIsAdded(val responseMessage: String) : BehaviorState()
    data class TrackIsNotAdded(val responseMessage: String) : BehaviorState()
}