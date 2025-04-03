package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    fun search(term: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(data: List<Track>?, errorMessage: String?)
    }
}