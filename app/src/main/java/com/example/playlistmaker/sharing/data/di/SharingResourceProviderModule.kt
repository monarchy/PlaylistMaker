package com.example.playlistmaker.sharing.data.di

import com.example.playlistmaker.sharing.data.impl.SharingResourcesProviderImpl
import com.example.playlistmaker.sharing.domain.SharingResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingResourcesProviderModule = module {
    single<SharingResourcesProvider>{
        SharingResourcesProviderImpl(androidContext())
    }
}