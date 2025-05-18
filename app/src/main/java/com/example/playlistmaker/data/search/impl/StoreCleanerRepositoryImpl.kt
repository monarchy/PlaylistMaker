package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.StoreCleanerRepository

class StoreCleanerRepositoryImpl(private val searchHistoryPreferences: SharedPreferences) :
    StoreCleanerRepository {

    override fun cleanStore() {
        searchHistoryPreferences.edit().clear().apply()
    }

}