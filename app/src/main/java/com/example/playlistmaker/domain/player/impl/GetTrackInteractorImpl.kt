package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.player.GetTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.GetTrackInteractor

class GetTrackInteractorImpl(private val getTrackRepository: GetTrackRepository):GetTrackInteractor {
    override fun get(stringJson: String, onComplete: (Track) -> Unit) {
        onComplete(getTrackRepository.get(stringJson))
    }

    override fun getWithoutCallback(string: String): Track {
        return getTrackRepository.get(string)
    }
}