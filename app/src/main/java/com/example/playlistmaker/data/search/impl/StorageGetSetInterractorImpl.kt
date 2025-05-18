package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.StoreGetSetRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.StorageGetSetInterractor

class StorageGetSetInterractorImpl(private val storeGetSetRepository: StoreGetSetRepository) :
    StorageGetSetInterractor {

    override fun saveTrackInHistory(track: Track) {
        storeGetSetRepository.saveTrackInHistory(track)
    }

    override fun getSearchHistory(): MutableList<Track> {
        return storeGetSetRepository.getSearchHistory()
    }

    override fun getPreferences(): SharedPreferences {
        return storeGetSetRepository.getPreferences()
    }
}