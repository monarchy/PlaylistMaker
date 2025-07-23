package com.example.playlistmaker.domain.player

import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface PlayerInterractor {

    val mediaPlayerState: StateFlow<MediaPlayerState>

    fun playBackControl(scope: CoroutineScope)

    fun preparePlayer(trackPreviewUrl: String)

    fun release()

    fun pauseMusic()

}
