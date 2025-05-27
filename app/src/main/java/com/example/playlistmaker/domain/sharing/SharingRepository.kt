package com.example.playlistmaker.domain.sharing

interface SharingRepository {
    fun shareApp(link: String)
    fun openTerms()
    fun openSupport()
}