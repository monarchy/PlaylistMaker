package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(trackId: Long)
    fun getIdFavoriteTracks():Flow<List<Long>>
}