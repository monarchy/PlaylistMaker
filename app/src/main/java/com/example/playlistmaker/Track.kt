package com.example.playlistmaker

data class Track(val trackId: Long, val trackName: String,
                 val artistName: String, val trackTime: Long, val artworkUrl100: String?,
                 val collectionName: String?, val releaseDate: String?,
                 val primaryGenreName: String?, val country: String?)
