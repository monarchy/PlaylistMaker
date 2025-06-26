package com.example.playlistmaker.domain.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track

interface StoreGetSetRepository {

    fun saveTrackInHistory(track: Track)

    fun getSearchHistory(): MutableList<Track>

    fun getPreferences(): SharedPreferences

}