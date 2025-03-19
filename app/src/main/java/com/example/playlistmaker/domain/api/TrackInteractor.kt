package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun search(term: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(data: List<Track>?)
        fun onError(error: Throwable)
    }
}