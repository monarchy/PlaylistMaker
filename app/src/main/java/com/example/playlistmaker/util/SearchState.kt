package com.example.playlistmaker.util

import com.example.playlistmaker.domain.models.Track

sealed class SearchState {
    data class ShowSearchContent(val tracks: List<Track>): SearchState()
    data class ShowHistoryContent(val tracks: List<Track>): SearchState()
    data object Loading : SearchState()
    data object NothingFound: SearchState()
    data object NetworkError: SearchState()
    data object EmptyScreen: SearchState()
}