package com.example.playlistmaker.util

sealed class MediaPlayerState(val isPlayButtonEnabled: Boolean,val progress: String) {
    companion object{
        private const val TIME_DEF = "00:00"
    }
    class Default : MediaPlayerState(false, TIME_DEF)
    class Prepared() : MediaPlayerState(true, TIME_DEF)
    class Playing(progress: String) : MediaPlayerState(true, progress)
    class Paused(progress: String) : MediaPlayerState(true, progress)
}