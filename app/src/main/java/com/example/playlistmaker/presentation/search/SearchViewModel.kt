package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.domain.search.StorageGetSetInterractor
import com.example.playlistmaker.domain.search.StoreCleanerInterractor
import com.example.playlistmaker.util.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val storeGetSetInterractor: StorageGetSetInterractor,
    private var cleanSearchHistory: StoreCleanerInterractor?,
    private var trackSearchInteractor: SearchTrackInteractor?,
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        cleanSearchHistory = null
        trackSearchInteractor = null
    }

    var currentQuery = ""
    var searchDebounceJob: Job? = null
    private val _screenState = MutableLiveData<SearchState>(SearchState.Loading)
    val screenState: LiveData<SearchState> get() = _screenState

    init {
        val trackSet = storeGetSetInterractor.getSearchHistory()
        if (trackSet.isEmpty()) {
            _screenState.postValue(SearchState.EmptyScreen)
        } else _screenState.postValue(SearchState.ShowHistoryContent(trackSet))
    }

    fun clearSearchRequest() {
        val trackHistory = storeGetSetInterractor?.getSearchHistory()
        if (trackHistory?.isNotEmpty() == true)
            _screenState.postValue(SearchState.ShowHistoryContent(trackHistory))
        else _screenState.postValue(SearchState.EmptyScreen)
    }

    fun onTrackClicked(track: Track) {
        storeGetSetInterractor.saveTrackInHistory(track)
    }

    fun onClickSearchClear() {
        storeGetSetInterractor.getPreferences()?.let { cleanSearchHistory?.execute() }
        _screenState.postValue(SearchState.EmptyScreen)
    }

    fun searchRequestDebounce(query: String) {
        if (query.isNotEmpty() && query != currentQuery) {
            currentQuery = query
            _screenState.postValue(SearchState.Loading)
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
        _screenState.postValue(SearchState.Loading)
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            trackSearchInteractor?.searchTracks(query)?.collect { pair ->
                processResult(pair.first, pair.second)
            }
        }
    }

    fun processResult(foundTrack: List<Track>?, errorState: SearchState?) {
        when {
            errorState != null -> {
                _screenState.postValue(errorState!!)
            }

            foundTrack!!.isEmpty() -> {
                _screenState.postValue(SearchState.NothingFound)
            }

            foundTrack.isNotEmpty() -> {
                _screenState.postValue(SearchState.ShowSearchContent(foundTrack))
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
