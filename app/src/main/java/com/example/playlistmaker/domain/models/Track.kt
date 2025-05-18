package com.example.playlistmaker.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    var artworkUrl100: String,
    val collectionName: String,
    val releaseDate:String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)