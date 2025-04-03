package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun switchTheme(isDarkTheme: Boolean)
    fun isDarkThemeEnabled(): Boolean
}