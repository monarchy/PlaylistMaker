package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.DatabaseRepositoryImpl
import com.example.playlistmaker.data.player.UserMediaPlayerRepository
import com.example.playlistmaker.data.player.impl.UserMediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.data.search.SearchTrackRepository
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchTrackRepositoryImpl
import com.example.playlistmaker.data.settings.ThemeChangerRepository
import com.example.playlistmaker.data.settings.impl.ThemeChangerRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingRepository
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.db.DatabaseRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("history_preferences")),get())
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
        SearchTrackRepositoryImpl(get(), get())
    }

    single<DatabaseRepository>{
        DatabaseRepositoryImpl(get(),get())
    }

    factory {TrackDbConverter()}
}