package com.example.playlistmaker.search.data.di

import android.content.Context
import com.example.playlistmaker.search.ResourcesProvider
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.ResourcesProviderImpl
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.PlaylistApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkService
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.util.Constants
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {

    single<PlaylistApi> {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaylistApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
    }

    single<ResourcesProvider> {
        ResourcesProviderImpl(androidContext())
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkService(androidContext(), get())
    }

}