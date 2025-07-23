package com.example.playlistmaker.domain.db.favorite

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteControlInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun favoriteControl(track: Track)
    fun syncingWithFavoriteTracks(searchList: List<Track>): Flow<List<Track>>
}