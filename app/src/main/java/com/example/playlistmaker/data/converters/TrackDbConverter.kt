package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entities.TracksEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {
    fun map(trackEntity: TracksEntity): Track {
        return Track(
            trackEntity.isFavorite,
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl
        )
    }

    fun map(track: Track): TracksEntity {
        return TracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun map(trackList: List<TracksEntity>): List<Track> {
        return trackList.map { track ->
            Track(
                isFavorite = track.isFavorite,
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl
            )
        }
    }
}