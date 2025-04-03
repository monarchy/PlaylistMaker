package com.example.playlistmaker.main.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.main.domain.impl.InternalNavigation
import com.example.playlistmaker.util.Creator

class MainViewModel(private val internalNavigation: InternalNavigation) : ViewModel() {

    companion object {

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                MainViewModel(
                    Creator.provideInternalNavigator(context)
                )
            }
        }
    }

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