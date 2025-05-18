package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.StoreGetSetRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.GsonClient


class StoreGetSetRepositoryImpl(private val searchHistoryPreferences: SharedPreferences) : StoreGetSetRepository {

    override fun saveTrackInHistory(track: Track) {
        val searchHistory = getSearchHistory().apply {
            remove(track)
            add(0, track)
            if (size > MAX_COUNT_SEARCH_HISTORY) removeAt(size - 1)
        }
        val json = GsonClient.listToJson(searchHistory)
        searchHistoryPreferences.edit().putString(HISTORY_PREFERENCES_KEY, json).apply()
    }

    override fun getSearchHistory(): MutableList<Track> {
        val json = searchHistoryPreferences.getString(HISTORY_PREFERENCES_KEY, "") ?: ""
        if (json.isNotEmpty()) {
            val trackHistory = GsonClient.arrayFromJson(json)
            return trackHistory
        } else return mutableListOf<Track>()
    }

    override fun getPreferences(): SharedPreferences {
        return searchHistoryPreferences
    }

    companion object {
        const val HISTORY_PREFERENCES_KEY = "history_list"
        private const val MAX_COUNT_SEARCH_HISTORY = 10
    }
}