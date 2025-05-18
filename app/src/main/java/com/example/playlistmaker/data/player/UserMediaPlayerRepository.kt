package com.example.playlistmaker.data.player

import androidx.lifecycle.LiveData
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope

interface UserMediaPlayerRepository {

    val mediaPlayerState: LiveData<MediaPlayerState>

    fun pauseMusic()

    fun playMusic(scope: CoroutineScope)

    fun startTimer(scope: CoroutineScope)

    fun playbackControl(scope: CoroutineScope)

    fun preparePlayer(trackPreviewUrl: String)

    fun release() {}

    fun getPlayTimer():String

    fun reset()

}