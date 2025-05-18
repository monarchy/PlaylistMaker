package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTrackRepository {
    fun searchTracks(expression: String): Flow<com.example.playlistmaker.util.Resource<List<Track>>>
}