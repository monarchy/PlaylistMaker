package com.example.playlistmaker.data.db.favorite

import androidx.room.withTransaction
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entities.TracksEntity
import com.example.playlistmaker.domain.db.favorite.FavoriteControlRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteControlRepositoryImpl(
    private val db: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteControlRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = db.trackDao().getAllFavoriteTracks()
        .map { entities ->
            convertFromTrackEntity(entities)
        }

    override suspend fun toggleFavoriteTrack(track: Track) {
        val trackEntity = convertToTrackEntity(track)
        db.withTransaction {
            val isContainsInPlaylist: Boolean =
                db.connectionTableDao().findTrackInConnectionTable(trackEntity.trackId)
            when {
                isContainsInPlaylist -> db.trackDao().addTrackToTrackTable(trackEntity)
                !trackEntity.isFavorite -> db.trackDao().deleteTrackFromTable(trackEntity.trackId)
                else -> db.trackDao().addTrackToTrackTable(trackEntity)
            }
        }
    }

    override fun getIdFavoriteTracks(): Flow<List<Long>> {
        return db.trackDao().getAllFavoriteTracksId()
    }

    private fun convertFromTrackEntity(tracks: List<TracksEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TracksEntity {
        return trackDbConverter.map(track)
    }
}