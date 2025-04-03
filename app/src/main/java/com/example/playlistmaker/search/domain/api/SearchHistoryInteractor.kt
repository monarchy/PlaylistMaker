package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun saveTrackToPrefs(track: Track, timestamp: Long)
    fun getHistory(): ArrayList<Track>
    fun cleanHistory()
    fun loadHistoryFromPrefs()

}