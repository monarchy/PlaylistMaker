package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.ResourcesProviderImpl
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.PlaylistApi
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkService
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.SharingResourcesProviderImpl
import com.example.playlistmaker.sharing.domain.SharingResourcesProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val SEARCH_HISTORY = "search_history"
    private const val MODE_PRIVATE = Context.MODE_PRIVATE

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(
            RetrofitNetworkService(
                context,
                provideTrackApi(provideRetrofit())
            )
        )
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun createSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPrefs: SharedPreferences =
            context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val repository = createSearchHistoryRepository(context)
        return SearchHistoryInteractorImpl(repository)
    }

    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(applicationContext)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun provideSharingResourcesProvider(context: Context): SharingResourcesProvider {
        return SharingResourcesProviderImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            provideExternalNavigator(context),
            provideSharingResourcesProvider(context)
        )
    }

    fun provideInternalNavigator(context: Context): InternalNavigation {
        return InternalNavigationImpl(context)
    }

    fun provideResourcesProvider(context: Context): ResourcesProviderImpl {
        return ResourcesProviderImpl(context)
    }

    private const val baseUrl = "https://itunes.apple.com"

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideTrackApi(retrofit: Retrofit): PlaylistApi {
        return retrofit.create(PlaylistApi::class.java)
    }
}