package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import kotlinx.coroutines.flow.Flow

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) :
    SearchHistoryInteractor {

    override suspend fun saveTrackInHistory(track: Track) {
        searchHistoryRepository.saveTrackInHistory(track)
    }

    override suspend fun getSearchHistory(): Flow<List<Track>> {
        return searchHistoryRepository.getSearchHistory()
    }

    override fun cleanStore() {
        searchHistoryRepository.cleanStore()
    }
}