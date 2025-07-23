package com.example.playlistmaker.presentation.playlist_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.playlist_forms.states.PlaylistCreateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class PlaylistCreateViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    protected open val _playlistFormState =
        MutableStateFlow<PlaylistCreateState>(PlaylistCreateState.Default())

    val playlistFormState: StateFlow<PlaylistCreateState> get() = _playlistFormState

    open fun playlistApply(playlistName: String, playlistTitle: String?, imagePath: String?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(
                convertToPlaylist(
                    playlistName,
                    playlistTitle,
                    imagePath
                )
            ).collect { result ->
                when {
                    result.isSuccess -> {
                        val successMessage = result.getOrNull() ?: "Плейлист создан"
                        _playlistFormState.value =
                            PlaylistCreateState.Success(successMessage)
                    }

                    result.isFailure -> {
                        val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                        _playlistFormState.value =
                            PlaylistCreateState.Denied(errorMessage)
                    }
                }
            }
        }
    }

    private fun convertToPlaylist(
        playlistName: String,
        playlistTitle: String?,
        posterId: String?
    ): Playlist {
        return Playlist(0, playlistName, playlistTitle, posterId, 0)
    }
}