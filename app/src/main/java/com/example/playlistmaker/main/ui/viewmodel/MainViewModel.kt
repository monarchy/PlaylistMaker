package com.example.playlistmaker.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.domain.impl.InternalNavigation

class MainViewModel(private val internalNavigation: InternalNavigation) : ViewModel() {



    fun startSearch() {
        internalNavigation.startSearch()
    }

    fun startMedia() {
        internalNavigation.startMedia()
    }

    fun startSettings() {
        internalNavigation.startSettings()
    }

}