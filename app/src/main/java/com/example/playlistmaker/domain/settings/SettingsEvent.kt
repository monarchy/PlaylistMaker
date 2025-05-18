package com.example.playlistmaker.domain.settings

sealed class SettingsEvent {
    data class SwapTheme(val boolean: Boolean) : SettingsEvent()
    data object OpenTerms : SettingsEvent()
    data object OpenSupport : SettingsEvent()
    data object ShareApp : SettingsEvent()

}