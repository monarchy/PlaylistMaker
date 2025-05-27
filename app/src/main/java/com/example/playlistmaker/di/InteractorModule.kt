package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.search.impl.StorageGetSetInterractorImpl
import com.example.playlistmaker.domain.player.GetTrackInteractor
import com.example.playlistmaker.domain.player.PlayerInterractor
import com.example.playlistmaker.domain.player.impl.GetTrackInteractorImpl
import com.example.playlistmaker.domain.player.impl.PlayerInterractorImpl
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.domain.search.StorageGetSetInterractor
import com.example.playlistmaker.domain.search.StoreCleanerInterractor
import com.example.playlistmaker.domain.search.impl.SearchTrackInteractorImpl
import com.example.playlistmaker.domain.search.impl.StoreCleanerInterractorImpl
import com.example.playlistmaker.domain.settings.ThemeChangerInteractor
import com.example.playlistmaker.domain.settings.impl.ThemeChangerInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
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

    factory<SharingInteractor> {(context: Context) ->
        SharingInteractorImpl(get{ parametersOf(context) })
    }

    factory<StorageGetSetInterractor> {
        StorageGetSetInterractorImpl(get())
    }

    factory<StoreCleanerInterractor> {
        StoreCleanerInterractorImpl(get())
    }

    factory<GetTrackInteractor> {
        GetTrackInteractorImpl(get())
    }
}