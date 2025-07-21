package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.db.favorite.FavoriteControlRepositoryImpl
import com.example.playlistmaker.data.db.playlist.PlaylistDbRepositoryImpl
import com.example.playlistmaker.data.image_storage.ImageStorageRepositoryImpl
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
import com.example.playlistmaker.domain.db.favorite.FavoriteControlRepository
import com.example.playlistmaker.domain.db.playlist.PlaylistDbRepository
import com.example.playlistmaker.domain.image_storage.ImageStorageRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named("history_preferences")), get())
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

    single<SearchTrackRepository> {
        SearchTrackRepositoryImpl(get(), get())
    }

    single<FavoriteControlRepository> {
        FavoriteControlRepositoryImpl(get(), get())
    }

    single<PlaylistDbRepository> {
        PlaylistDbRepositoryImpl(get(), get(), get())
    }

    single<ImageStorageRepository> {
        ImageStorageRepositoryImpl(get(), get())
    }
}