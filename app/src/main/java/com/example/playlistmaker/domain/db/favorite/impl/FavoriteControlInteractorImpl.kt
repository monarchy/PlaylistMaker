package com.example.playlistmaker.domain.db.favorite.impl

import com.example.playlistmaker.domain.db.favorite.FavoriteControlInteractor
import com.example.playlistmaker.domain.db.favorite.FavoriteControlRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FavoriteControlInteractorImpl(private val favoriteRepository: FavoriteControlRepository) :
    FavoriteControlInteractor {
    override fun getFavoriteTracks(): Flow<List<Track>> = favoriteRepository.getFavoriteTracks()

    override suspend fun favoriteControl(track: Track) {
        favoriteRepository.toggleFavoriteTrack(track)
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