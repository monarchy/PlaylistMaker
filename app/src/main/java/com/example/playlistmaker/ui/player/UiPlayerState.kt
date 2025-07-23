package com.example.playlistmaker.ui.player

sealed class UiPlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String,
    val isFavorite: Boolean
) {
    class Default(isPlayButtonEnabled: Boolean, progress: String, isFavorite: Boolean) :
        UiPlayerState(isPlayButtonEnabled, progress, isFavorite)

    class Prepared(isPlayButtonEnabled: Boolean, progress: String, isFavorite: Boolean) :
        UiPlayerState(isPlayButtonEnabled, progress, isFavorite)

    class Playing(isPlayButtonEnabled: Boolean, progress: String, isFavorite: Boolean) :
        UiPlayerState(isPlayButtonEnabled, progress, isFavorite)

    class Paused(isPlayButtonEnabled: Boolean, progress: String, isFavorite: Boolean) :
        UiPlayerState(isPlayButtonEnabled, progress, isFavorite)
}
