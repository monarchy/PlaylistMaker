package com.example.playlistmaker.domain.db.favorite

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteControlRepository {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun toggleFavoriteTrack(track: Track)
    fun getIdFavoriteTracks(): Flow<List<Long>>
}