package com.example.playlistmaker.domain.models

data class PlaylistWithTracks(
    val playlistInfo: Playlist,
    val trackList: List<Track>
)