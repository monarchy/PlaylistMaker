package com.example.playlistmaker.settings.data.di

import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val settingsRepositoryModule = module {

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}