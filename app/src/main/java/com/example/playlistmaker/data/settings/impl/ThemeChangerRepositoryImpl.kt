package com.example.playlistmaker.data.settings.impl


import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.settings.ThemeChangerRepository

class ThemeChangerRepositoryImpl(private val darkThemePreferences: SharedPreferences): ThemeChangerRepository {

    override fun switchTheme(themeStatus: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (themeStatus) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        darkThemePreferences.edit().putBoolean(DARK_THEME, themeStatus).apply()
    }

    override fun getThemeStatus(): Boolean {
        return darkThemePreferences.getBoolean(DARK_THEME, false)
    }

    companion object {
        private const val DARK_THEME = "dark_key"
    }
}