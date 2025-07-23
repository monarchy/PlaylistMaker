package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.GsonClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val searchHistoryPreferences: SharedPreferences,
    private val database: AppDatabase
) :
    SearchHistoryRepository {
    override suspend fun saveTrackInHistory(track: Track) {
        val searchHistory = getSearchHistory()
            .first()
            .let { currentList ->
                listOf(track) + currentList.filterNot { it.trackId == track.trackId }
            }
            .take(MAX_COUNT_SEARCH_HISTORY)
        withContext(Dispatchers.IO) {
            val json = GsonClient.trackListToJson(searchHistory)
            searchHistoryPreferences
                .edit()
                .putString(HISTORY_PREFERENCES_KEY, json)
                .commit()
        }
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        val json =
            searchHistoryPreferences.getString(
                HISTORY_PREFERENCES_KEY,
                ""
            ) ?: ""
        val history = if (json.isNotEmpty()) {
            GsonClient.trackListFromJson(json).let { tracks ->
                val favoritesId = database.trackDao().getAllFavoriteTracksId().first().toSet()
                tracks.map { track ->
                    track.copy(isFavorite = favoritesId.contains(track.trackId))
                }
            }
        } else emptyList()
        emit(history)
    }.flowOn(Dispatchers.IO)

    override fun cleanStore() {
        searchHistoryPreferences.edit().clear().apply()
    }

    companion object {
        const val HISTORY_PREFERENCES_KEY = "history_list"
        private const val MAX_COUNT_SEARCH_HISTORY = 10
    }
}