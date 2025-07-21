package com.example.playlistmaker.data.settings

interface ThemeChangerRepository {
    fun switchTheme(check:Boolean)
    fun getThemeStatus(): Boolean
}