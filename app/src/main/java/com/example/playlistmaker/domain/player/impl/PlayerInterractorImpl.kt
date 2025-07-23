package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class PlayerInterractorImpl(
    private val userMediaPlayerImpl: UserMediaPlayerRepository,
) : PlayerInterractor {
    override val mediaPlayerState: StateFlow<MediaPlayerState> =
        userMediaPlayerImpl.mediaPlayerState

    override fun playBackControl(scope: CoroutineScope) {
        userMediaPlayerImpl.playbackControl(scope)
    }

    override fun preparePlayer(trackPreviewUrl: String) {
        userMediaPlayerImpl.preparePlayer(trackPreviewUrl)
    }

    override fun release() {
        userMediaPlayerImpl.release()
    }

    override fun pauseMusic() {
        userMediaPlayerImpl.pauseMusic()
    }

}