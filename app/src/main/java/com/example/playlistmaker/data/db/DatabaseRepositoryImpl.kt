package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.domain.db.DatabaseRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DatabaseRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : DatabaseRepository {

    override fun getFavoriteTracks(): Flow<List<Track>>{
        return database.trackDao().getTracks()
            .map { entities -> convertFromTrackEntity(entities) }
    }

    override suspend fun addToFavorite(track: Track) {
        database.trackDao().insertTrack(convertToTrackEntity(track))
    }

    override suspend fun removeFromFavorite(trackId: Long) {
        database.trackDao().deleteTrack(trackId)
    }

    override fun getIdFavoriteTracks(): Flow<List<Long>> = flow {
        val favoriteId = database.trackDao().getTrackId()
        emit(favoriteId)
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTracksEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavoriteTracksEntity {
        return trackDbConverter.map(track)
    }
}