package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.db.favorite.FavoriteControlInteractor
import com.example.playlistmaker.domain.db.favorite.impl.FavoriteControlInteractorImpl
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractorImpl
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.domain.player.impl.PlayerInterractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.domain.search.impl.SearchTrackInteractorImpl
import com.example.playlistmaker.domain.settings.ThemeChangerInteractor
import com.example.playlistmaker.domain.settings.impl.ThemeChangerInteractorImpl
import com.example.playlistmaker.domain.shairing.settings.SharingInteractor
import com.example.playlistmaker.domain.shairing.settings.impl.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val interactorModule = module {

    factory<PlayerInterractor> {
        PlayerInterractorImpl(get())
    }

    factory<SearchTrackInteractor> {
        SearchTrackInteractorImpl(get())
    }

    factory<ThemeChangerInteractor> {
        ThemeChangerInteractorImpl(get())
    }

    factory<SharingInteractor> { (context: Context) ->
        SharingInteractorImpl(get { parametersOf(context) })
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<FavoriteControlInteractor> {
        FavoriteControlInteractorImpl(get())
    }

    factory<PlaylistInteractor> {(context: Context) ->
        PlaylistInteractorImpl(get(), get(), get{ parametersOf(context) })
    }
}