package com.example.playlistmaker.data.sharing

import android.content.Context

interface SharingRepository {
    fun shareApp()
    fun openTerms()
    fun openSupport()
}