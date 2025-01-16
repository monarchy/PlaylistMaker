package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryAdapter(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val HISTORY_KEY = "history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }
        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}