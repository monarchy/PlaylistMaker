package com.example.playlistmaker.ui.playlist_create_form

sealed class PlaylistCreateState(val createMessage: String?) {
    data class Default(val default: Nothing? = null) : PlaylistCreateState(default)
    data class Success(val successMessage: String) : PlaylistCreateState(successMessage)
    data class Denied(val errorMessage: String) : PlaylistCreateState(errorMessage)
}