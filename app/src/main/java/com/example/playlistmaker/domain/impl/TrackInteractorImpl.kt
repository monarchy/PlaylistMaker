package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(term: String, consumer: TrackInteractor.TrackConsumer) {
        try {
            executor.execute {
                consumer.consume(repository.searchTracks(term))
            }
        }
        catch (e: Exception) {
            consumer.onError(e)
        }
    }
}