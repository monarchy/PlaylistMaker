package com.example.playlistmaker.domain.settings

interface ThemeChangerInteractor {
    fun changeTheme(themeStatus: Boolean)
    fun getThemeStatus(): Boolean
}