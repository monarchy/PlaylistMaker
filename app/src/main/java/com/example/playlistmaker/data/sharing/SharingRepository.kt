package com.example.playlistmaker.data.sharing

interface SharingRepository {
    fun shareApp(link: String)
    fun openTerms()
    fun openSupport()
}