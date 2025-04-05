package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
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
            sharedPrefs.edit() {
                remove(removedTrack.trackId.toString())
            }
        }

        saveTrackToPrefs(track, System.currentTimeMillis())
    }

    override fun saveTrackToPrefs(track: Track, timestamp: Long) {
        val json = Gson().toJson(track)
        sharedPrefs.edit() {
            putString(track.trackId.toString(), "$timestamp|$json")
            }
    }

    override fun getHistory(): ArrayList<Track> {
        return searchList
    }

    override fun cleanHistory() {
        sharedPrefs.edit() {
            clear()
        }

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
                } catch (_: Exception) {
                    null
                }
            }.sortedByDescending { it.first }
                .map { it.second }
        )

        while (searchList.size > maxSize) {
            val removedTrack = searchList.removeAt(searchList.size - 1)
            sharedPrefs.edit() {
                remove(removedTrack.trackId.toString())
            }
        }
    }
}