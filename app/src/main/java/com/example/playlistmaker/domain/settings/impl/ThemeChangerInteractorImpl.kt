package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.ThemeChangerRepository
import com.example.playlistmaker.domain.settings.ThemeChangerInteractor

class ThemeChangerInteractorImpl(private val themeChangerRepository: ThemeChangerRepository):ThemeChangerInteractor {
    override fun changeTheme(themeStatus:Boolean) {
        themeChangerRepository.switchTheme(themeStatus)
    }

    override fun getThemeStatus(): Boolean {
        return themeChangerRepository.getThemeStatus()
    }
}