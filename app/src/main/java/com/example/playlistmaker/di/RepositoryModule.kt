package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.player.GetTrackRepository
import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.data.player.impl.GetTrackRepositoryImpl
import com.example.playlistmaker.data.player.impl.UserMediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchTrackRepository
import com.example.playlistmaker.data.search.StoreCleanerRepository
import com.example.playlistmaker.data.search.StoreGetSetRepository
import com.example.playlistmaker.data.search.impl.SearchTrackRepositoryImpl
import com.example.playlistmaker.data.search.impl.StoreCleanerRepositoryImpl
import com.example.playlistmaker.data.search.impl.StoreGetSetRepositoryImpl
import com.example.playlistmaker.data.settings.ThemeChangerRepository
import com.example.playlistmaker.data.settings.impl.ThemeChangerRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingRepository
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<StoreGetSetRepository> {
        StoreGetSetRepositoryImpl(get(named("history_preferences")))
    }

    single<StoreCleanerRepository> {
        StoreCleanerRepositoryImpl(get(named("history_preferences")))
    }

    single<ThemeChangerRepository> {
        ThemeChangerRepositoryImpl(get(named("dark_preferences")))
    }

    factory<UserMediaPlayerRepository> {
        UserMediaPlayerRepositoryImpl(get())
    }

    single<SharingRepository> { (context: Context) ->
        SharingRepositoryImpl(context)
    }

    single<SearchTrackRepository>{
        SearchTrackRepositoryImpl(get())
    }

    single<GetTrackRepository> {
        GetTrackRepositoryImpl(get())
    }
}