package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface DatabaseInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(trackId: Long)
    fun syncingWithFavoriteTracks(searchList: List<Track>):Flow<List<Track>>
}