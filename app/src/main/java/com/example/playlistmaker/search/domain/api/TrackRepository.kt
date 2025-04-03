package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}