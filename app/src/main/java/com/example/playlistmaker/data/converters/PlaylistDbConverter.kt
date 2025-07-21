package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.PlaylistWithTracksDb
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks

class PlaylistDbConverter {

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            playlistName = playlistEntity.playlistName,
            playlistTitle = playlistEntity.playlistTitle,
            imagePath = playlistEntity.imagePath,
            tracksCount = playlistEntity.tracksCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistTitle = playlist.playlistTitle,
            imagePath = playlist.imagePath,
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlistWithTracksDb: PlaylistWithTracksDb): PlaylistWithTracks {
        return PlaylistWithTracks(
            playlistInfo = map(playlistWithTracksDb.playlist),
            trackList = playlistWithTracksDb.tracks.map { trackEntity ->
                TrackDbConverter().map(trackEntity)
            }
        )
    }
}