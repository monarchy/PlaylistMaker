package com.example.playlistmaker.ui.playlist_forms.states

sealed interface PlaylistCreateState {
    data class Default(val default: Nothing? = null) : PlaylistCreateState
    data class Success(val successMessage: String) : PlaylistCreateState
    data class Denied(val errorMessage: String) : PlaylistCreateState
}