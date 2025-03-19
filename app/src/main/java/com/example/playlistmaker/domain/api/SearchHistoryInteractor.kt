package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun saveTrackToPrefs(track: Track, timestamp: Long)
    fun getHistory(): ArrayList<Track>
    fun cleanHistory()
    fun loadHistoryFromPrefs()

}