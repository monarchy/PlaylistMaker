package com.example.playlistmaker.search.domain.di

import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackRepository
import org.koin.dsl.module

val trackRepositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

}