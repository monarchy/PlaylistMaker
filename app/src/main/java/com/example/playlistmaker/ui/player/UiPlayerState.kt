package com.example.playlistmaker.ui.player

import com.example.playlistmaker.domain.models.Track

sealed class UiPlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String,
    val track: Track
) {
    class Default(
        isPlayButtonEnabled: Boolean,
        progress: String,
        track: Track
    ) : UiPlayerState(isPlayButtonEnabled, progress, track)

    class Prepared(
        isPlayButtonEnabled: Boolean,
        progress: String,
        track: Track
    ) : UiPlayerState(isPlayButtonEnabled, progress, track)

    class Playing(
        isPlayButtonEnabled: Boolean,
        progress: String,
        track: Track
    ) : UiPlayerState(isPlayButtonEnabled, progress, track)

    class Paused(
        isPlayButtonEnabled: Boolean,
        progress: String,
        track: Track
    ) : UiPlayerState(isPlayButtonEnabled, progress, track)
}