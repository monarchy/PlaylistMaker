package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.SearchState
import kotlinx.coroutines.flow.Flow

interface SearchTrackInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, SearchState?>>
}