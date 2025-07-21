package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.library.LibraryFragmentViewModel
import com.example.playlistmaker.presentation.library.MediaLibraryViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.playlist_create.PlaylistCreateViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModel = module {

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel { (context: Context) ->
        SearchViewModel(get(), get { parametersOf(context) }, get())
    }

    viewModel { (context: Context) ->
        SettingsViewModel(get { parametersOf(context) }, get { parametersOf(context) })
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        LibraryFragmentViewModel(get())
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel {
        PlaylistCreateViewModel(get())
    }
}