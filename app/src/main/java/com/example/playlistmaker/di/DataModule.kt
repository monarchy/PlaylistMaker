package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.util.GsonClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PreferenceFiles {
    const val HISTORY_PREFERENCES = "history_preferences"
    const val DARK_PREFERENCES = "dark_preferences"
    const val FIRST_LAUNCH = "is_first_launch"
}

object ApiConfig{
    const val BASE_URL = "https://itunes.apple.com/"
}

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }


    factory<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }


    single(named("history_preferences")) {
        androidContext()
            .getSharedPreferences(PreferenceFiles.HISTORY_PREFERENCES, Context.MODE_PRIVATE)
    }

    single(named("dark_preferences")) {
        androidContext()
            .getSharedPreferences(PreferenceFiles.DARK_PREFERENCES, Context.MODE_PRIVATE)
    }

    single(named("first_launch")) {
        androidContext()
            .getSharedPreferences(PreferenceFiles.FIRST_LAUNCH, Context.MODE_PRIVATE)
    }

    single { GsonClient }

    factory { MediaPlayer() }

}