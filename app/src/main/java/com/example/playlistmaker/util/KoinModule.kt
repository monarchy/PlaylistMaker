package com.example.playlistmaker.util

import com.example.playlistmaker.media.ui.viewmodel.FavouritesViewModel
import com.example.playlistmaker.media.ui.viewmodel.MediatekaViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistVewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaViewModules = module {
    viewModel { MediatekaViewModel() }
    viewModel { PlaylistVewModel() }
    viewModel { FavouritesViewModel() }
}