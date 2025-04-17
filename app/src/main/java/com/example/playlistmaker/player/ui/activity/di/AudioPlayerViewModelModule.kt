package com.example.playlistmaker.player.ui.activity.di

import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {
    viewModel {
        AudioPlayerViewModel()
    }
}