package com.example.playlistmaker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.DatabaseInteractor
import com.example.playlistmaker.ui.media_library.favorite.FavoriteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val databaseInteractor: DatabaseInteractor) : ViewModel() {

    private val _screenState = MutableStateFlow<FavoriteState>(FavoriteState.Empty)
    val screenState: StateFlow<FavoriteState> = _screenState

    init {
        viewModelScope.launch {
            getData()
        }
    }

    fun getData() {
        viewModelScope.launch {
            databaseInteractor.getFavoriteTracks().collect(){ trackList ->
                if (trackList.isEmpty()){
                    _screenState.value = FavoriteState.Empty
                }else _screenState.value = FavoriteState.Content(trackList)
            }
        }
    }
}