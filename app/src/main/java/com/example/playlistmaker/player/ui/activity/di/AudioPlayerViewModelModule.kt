package com.example.playlistmaker.player.ui.activity.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {
    factory { MediaPlayer() }
    viewModel { AudioPlayerViewModel(mediaPlayer = get()) }
}