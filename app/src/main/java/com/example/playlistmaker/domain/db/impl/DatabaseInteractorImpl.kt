package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.DatabaseInteractor
import com.example.playlistmaker.domain.db.DatabaseRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DatabaseInteractorImpl(private val favoriteRepository: DatabaseRepository):
    DatabaseInteractor {
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }

    override suspend fun addToFavorite(track: Track) {
        favoriteRepository.addToFavorite(track)
    }

    override suspend fun removeFromFavorite(trackId:Long) {
        favoriteRepository.removeFromFavorite(trackId)
    }

    override fun syncingWithFavoriteTracks(searchList: List<Track>): Flow<List<Track>> = flow {
        val sortedList = searchList.let { trackList ->
            val favoritesId = favoriteRepository.getIdFavoriteTracks().first().toSet()
            trackList.map { track ->
                track.copy(isFavorite = favoritesId.contains(track.trackId))
            }
        }
        emit(sortedList)
    }
}