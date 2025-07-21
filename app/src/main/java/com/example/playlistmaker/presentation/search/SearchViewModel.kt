package com.example.playlistmaker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.favorite.FavoriteControlInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.util.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private var trackSearchInteractor: SearchTrackInteractor?,
    private val databaseInteractor: FavoriteControlInteractor
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        trackSearchInteractor = null
        searchDebounceJob = null
    }

    var currentQuery = ""
    var searchDebounceJob: Job? = null
    private val _screenState = MutableStateFlow<SearchState>(SearchState.Loading)
    val screenState: StateFlow<SearchState> = _screenState
    private var isInitialized = false

    init {
        viewModelScope.launch {
            if (!isInitialized) {
                val firstLaunchContent = searchHistoryInteractor.getSearchHistory().first()
                if (firstLaunchContent.isEmpty()) {
                    _screenState.value = SearchState.EmptyScreen
                } else {
                    _screenState.value = SearchState.ShowHistoryContent(
                        firstLaunchContent
                    )
                }
                isInitialized = true
            }
        }
    }

    fun clickOnClearSearchRequest() {
        viewModelScope.launch {
            searchDebounceJob?.cancel()
            showSearchHistory(
                searchHistoryInteractor.getSearchHistory().first()
            )
        }
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.saveTrackInHistory(track)
        }
    }

    fun onClickSearchClear() {
        viewModelScope.launch {
            searchHistoryInteractor.cleanStore()
            showSearchHistory(searchHistoryInteractor.getSearchHistory().first())
        }
    }

    fun searchRequestDebounce(query: String) {
        if (query.isNotEmpty() && query != currentQuery) {
            currentQuery = query
            _screenState.value = SearchState.Loading
            searchDebounceJob?.cancel()
            searchDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                trackSearchInteractor?.searchTracks(query)?.collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    fun searchRequestUpdate(query: String) {
        currentQuery = query
        _screenState.value = SearchState.Loading
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            trackSearchInteractor?.searchTracks(query)?.collect { pair ->
                processResult(pair.first, pair.second)
            }
        }
    }

    fun resumeFragment() {
        viewModelScope.launch {
            when (val stateAfterResume = screenState.value) {
                SearchState.EmptyScreen -> onResumeStateIsEmptyScreen()
                SearchState.Loading -> onResumeStateIsLoading()
                SearchState.NetworkError -> onResumeStateIsErrorNetwork()
                SearchState.NothingFound -> onResumeStateIsNothingFound()
                is SearchState.ShowHistoryContent -> onResumeStateIsSearchHistory()
                is SearchState.ShowSearchContent -> onResumeStateIsSearchContent(stateAfterResume.tracks)
            }
        }
    }

    private fun showSearchHistory(searchHistory: List<Track>) {
        if (searchHistory.isEmpty()) {
            _screenState.value = SearchState.EmptyScreen
        } else {
            _screenState.value = SearchState.ShowHistoryContent(searchHistory)
        }
    }

    private fun onResumeStateIsEmptyScreen() {
        _screenState.value = SearchState.EmptyScreen
    }

    private fun onResumeStateIsNothingFound() {
        _screenState.value = SearchState.NothingFound
    }

    private fun onResumeStateIsErrorNetwork() {
        _screenState.value = SearchState.NetworkError
    }

    private fun onResumeStateIsLoading() {
        _screenState.value = SearchState.Loading
    }

    private fun onResumeStateIsSearchHistory() {
        viewModelScope.launch {
            val searchHistory = searchHistoryInteractor.getSearchHistory().first()
            _screenState.value =
                SearchState.ShowHistoryContent(searchHistory)
        }
    }

    private suspend fun onResumeStateIsSearchContent(list: List<Track>) {
        _screenState.value = SearchState.ShowSearchContent(
            databaseInteractor.syncingWithFavoriteTracks(
                list
            ).first()
        )
    }


    private fun processResult(foundTrack: List<Track>?, errorState: SearchState?) {
        when {
            errorState != null -> {
                _screenState.value = errorState!!
            }

            foundTrack!!.isEmpty() -> {
                _screenState.value = SearchState.NothingFound
            }

            foundTrack.isNotEmpty() -> {
                _screenState.value = SearchState.ShowSearchContent(foundTrack)
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
