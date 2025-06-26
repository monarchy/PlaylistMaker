package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.models.Track

interface GetTrackInteractor {
    fun get(stringJson: String, onComplete: (Track) -> Unit)
    fun getWithoutCallback(string: String):Track
}