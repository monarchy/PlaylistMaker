package com.example.playlistmaker.data.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    suspend fun saveTrackInHistory(track: Track)

    fun getSearchHistory(): Flow<List<Track>>

    fun cleanStore()
}