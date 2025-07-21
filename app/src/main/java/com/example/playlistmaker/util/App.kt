package com.example.playlistmaker.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModel
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    var darkTheme = false
    private val darkThemePreferences: SharedPreferences by lazy {
        this.getSharedPreferences(DARK_PREFERENCES, Context.MODE_PRIVATE)
    }
    private val firstLaunchFlag: SharedPreferences by lazy {
        this.getSharedPreferences(FIRST_LAUNCH, Context.MODE_PRIVATE)
    }


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModel)
        }
        PermissionRequester.initialize(applicationContext)
        instance = this

        val isFirstLaunch = isFirstLaunch()

        if (isFirstLaunch) {
            // Определяем системную тему сегмент if будет только один раз
            val isSystemDarkTheme = isSystemDarkThemeEnabled()
            switchDarkTheme(isSystemDarkTheme)
            // Сохраняем системную тему как пользовательскую настройку
            saveThemeStatus(isSystemDarkTheme)
            // Устанавливаем флаг, что первый запуск завершён
            setFlag(false)
        } else {
            // Используем сохранённые пользовательские настройки
            val darkThemeEnabled = getThemeStatus()
            switchDarkTheme(darkThemeEnabled)
        }
    }

    fun switchDarkTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveThemeStatus(darkThemeEnabled)
    }

    private fun isFirstLaunch(): Boolean {
        return firstLaunchFlag.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    private fun setFlag(status: Boolean) {
        firstLaunchFlag.edit().putBoolean(FIRST_LAUNCH_KEY, status).apply()
    }

    private fun isSystemDarkThemeEnabled(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun saveThemeStatus(value: Boolean) {
        darkThemePreferences.edit().putBoolean(DARK_THEME, value).apply()
    }

    fun getThemeStatus(): Boolean {
        return darkThemePreferences.getBoolean(DARK_THEME, false)
    }

    companion object {
        lateinit var instance: App
            private set

        private const val DARK_THEME = "dark_key"
        private const val DARK_PREFERENCES = "dark_preferences"

        private const val FIRST_LAUNCH = "is_first_launch"
        private const val FIRST_LAUNCH_KEY = "first_launch_key"
    }
}
