package com.example.playlistmaker.domain.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {

    suspend fun saveTrackInHistory(track: Track)

    suspend fun getSearchHistory(): Flow<List<Track>>

    fun cleanStore()
}