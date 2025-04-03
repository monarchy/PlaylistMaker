package com.example.playlistmaker.settings.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.util.Constants

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.THEME_SETTINGS, Context.MODE_PRIVATE)

    override fun switchTheme(isDarkTheme: Boolean) {
        sharedPref.edit().putBoolean(Constants.SWITCH_KEY, isDarkTheme).apply()
        applyTheme(isDarkTheme)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPref.getBoolean(Constants.SWITCH_KEY, false)
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}