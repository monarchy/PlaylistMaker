package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.SearchTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTrackInteractorImpl(private val repository: SearchTrackRepository) :
    SearchTrackInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, SearchState?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.errorState)
                }
            }
        }
    }
}