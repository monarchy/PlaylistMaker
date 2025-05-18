package com.example.playlistmaker.domain.player.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.util.MediaPlayerState
import kotlinx.coroutines.CoroutineScope

class PlayerInterractorImpl(
    private val userMediaPlayerImpl: UserMediaPlayerRepository,
) : PlayerInterractor {

    private var _mediaPlayerState = MutableLiveData<MediaPlayerState>()
    override val mediaPlayerState: LiveData<MediaPlayerState> get() = _mediaPlayerState

    init {
        userMediaPlayerImpl.mediaPlayerState.observeForever { state ->
            _mediaPlayerState.postValue(state)
        }
    }

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