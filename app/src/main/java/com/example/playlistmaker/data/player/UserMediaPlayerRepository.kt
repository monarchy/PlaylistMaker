package com.example.playlistmaker.data.player

import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


interface UserMediaPlayerRepository {

    val mediaPlayerState: StateFlow<MediaPlayerState>

    fun pauseMusic()

    fun playMusic(scope: CoroutineScope)

    fun startTimer(scope: CoroutineScope)

    fun playbackControl(scope: CoroutineScope)

    fun preparePlayer(trackPreviewUrl: String)

    fun release() {}

    fun getPlayTimer(): String

    fun reset()

}