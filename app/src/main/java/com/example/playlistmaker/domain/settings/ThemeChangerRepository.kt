package com.example.playlistmaker.domain.settings

interface ThemeChangerRepository {
    fun switchTheme(check:Boolean)
    fun getThemeStatus(): Boolean
}