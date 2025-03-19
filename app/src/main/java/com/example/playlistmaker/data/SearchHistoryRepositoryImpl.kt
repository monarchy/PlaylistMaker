package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) : SearchHistoryRepository {
    private val searchList = ArrayList<Track>()
    private val maxSize = 10

    override fun addTrack(track: Track) {
        if (searchList.contains(track)) {
            searchList.remove(track)
        }

        searchList.add(0, track)

        if (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }

        saveTrackToPrefs(track, System.currentTimeMillis())
    }

    override fun saveTrackToPrefs(track: Track, timestamp: Long) {
        val json = Gson().toJson(track)
        sharedPrefs.edit()
            .putString(track.trackId.toString(), "$timestamp|$json")
            .apply()
    }

    override fun getHistory(): ArrayList<Track> {
        return searchList
    }

    override fun cleanHistory() {
        sharedPrefs.edit()
            .clear()
            .apply()

        searchList.clear()
    }

    override fun loadHistoryFromPrefs() {
        searchList.clear()

        searchList.addAll(
            sharedPrefs.all.values.mapNotNull { value ->
                try {
                    val (timestamp, json) = value.toString().split("|")
                    val track = Gson().fromJson(json, Track::class.java)
                    Pair(timestamp.toLong(), track)
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.first }
                .map { it.second }
        )

        while (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit()
                .remove(removedTrack.trackId.toString())
                .apply()
        }
    }
}