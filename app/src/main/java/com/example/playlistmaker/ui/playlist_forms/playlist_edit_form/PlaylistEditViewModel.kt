package com.example.playlistmaker.ui.playlist_forms.playlist_edit_form

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.playlist_create.PlaylistCreateViewModel
import com.example.playlistmaker.ui.playlist_forms.states.PlaylistCreateState
import com.example.playlistmaker.ui.playlist_forms.states.PlaylistEditState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val playlist: Playlist,
    private val playlistInteractor: PlaylistInteractor,
) :
    PlaylistCreateViewModel(playlistInteractor) {

    override val _playlistFormState =
        MutableStateFlow<PlaylistCreateState>(PlaylistEditState.OnEditing(playlist))


    override fun playlistApply(playlistName: String, playlistTitle: String?, imagePath: String?) {
        viewModelScope.launch {
            val playlistToUpdate = playlist.copy(
                playlistName = playlistName,
                playlistTitle = playlistTitle,
                imagePath = imagePath
            )
            playlistInteractor.updatePlaylistInfo(playlistToUpdate)
            _playlistFormState.value = PlaylistEditState.Success
        }
    }
}