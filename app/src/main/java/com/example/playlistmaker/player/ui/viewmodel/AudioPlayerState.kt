package com.example.playlistmaker.player.ui.viewmodel

sealed class AudioPlayerState {
    data class Default(
        val isPlayButtonEnabled: Boolean = false
    ) : AudioPlayerState()

    data class Prepared(
        val isPlayButtonEnabled: Boolean = true,
        val currentPosition: String = "00:00"
    ) : AudioPlayerState()

    data class Playing(
        val isPlayButtonEnabled: Boolean = true,
        val currentPosition: String
    ) : AudioPlayerState()

    data class Paused(
        val isPlayButtonEnabled: Boolean = true,
        val currentPosition: String
    ) : AudioPlayerState()
}