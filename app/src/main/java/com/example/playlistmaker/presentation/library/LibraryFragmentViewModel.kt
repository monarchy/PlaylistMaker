package com.example.playlistmaker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.ui.media_library.library.PlaylistLibraryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryFragmentViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _screenState = MutableStateFlow<PlaylistLibraryState>(PlaylistLibraryState.Empty)
    val screenState: StateFlow<PlaylistLibraryState> = _screenState

    init {
        viewModelScope.launch {
            getData()
        }
    }

    private fun getData() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect() { playlists ->
                if (playlists.isEmpty()) {
                    _screenState.value = PlaylistLibraryState.Empty
                } else _screenState.value = PlaylistLibraryState.Content(playlists)
            }
        }
    }
}