package com.example.playlistmaker.domain.player

import androidx.lifecycle.LiveData
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope

interface PlayerInterractor {

    val mediaPlayerState: LiveData<MediaPlayerState>

    fun playBackControl(scope: CoroutineScope)

    fun preparePlayer(trackPreviewUrl: String)

    fun release()

    fun pauseMusic()

}
