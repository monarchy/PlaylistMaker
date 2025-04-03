package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkService
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    private const val SEARCH_HISTORY = "search_history"
    private const val MODE_PRIVATE = Context.MODE_PRIVATE

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkService())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun createSearchHistoryRepository(context: Context) : SearchHistoryRepository {
        val sharedPrefs: SharedPreferences = context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val repository = createSearchHistoryRepository(context)
        return SearchHistoryInteractorImpl(repository)
    }
}