package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistTitle: String?,
    val imagePath: String?,
    val tracksCount: Long
)