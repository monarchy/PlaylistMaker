package com.example.playlistmaker.settings.domain.di

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val settingsInteractorModule = module {
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}
