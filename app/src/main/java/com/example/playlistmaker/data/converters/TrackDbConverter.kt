package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.FavoriteTracksEntity
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {
    fun map(trackEntity: FavoriteTracksEntity): Track {
        return Track(
            true,
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

    fun map(trackDto: TrackDto): FavoriteTracksEntity {
        return FavoriteTracksEntity(
            trackDto.trackId,
            trackDto.trackName,
            trackDto.artistName,
            trackDto.trackTimeMillis,
            trackDto.artworkUrl100,
            trackDto.collectionName,
            trackDto.releaseDate,
            trackDto.primaryGenreName,
            trackDto.country,
            trackDto.previewUrl
        )
    }

    fun map(track: Track): FavoriteTracksEntity {
        return FavoriteTracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}