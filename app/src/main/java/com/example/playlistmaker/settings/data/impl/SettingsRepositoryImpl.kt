package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.util.Constants

class SettingsRepositoryImpl( private val sharedPrefs: SharedPreferences) : SettingsRepository {


    override fun switchTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit() { putBoolean(Constants.SWITCH_KEY, isDarkTheme) }
        applyTheme(isDarkTheme)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(Constants.SWITCH_KEY, false)
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