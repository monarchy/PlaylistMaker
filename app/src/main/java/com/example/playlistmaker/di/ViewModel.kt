package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.library.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.library.LibraryFragmentViewModel
import com.example.playlistmaker.presentation.library.MediaLibraryViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.playlist.PlaylistViewModel
import com.example.playlistmaker.presentation.playlist_create.PlaylistCreateViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.ui.playlist_forms.playlist_edit_form.PlaylistEditViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModel = module {

    viewModel { (track: Track, context: Context) ->
        PlayerViewModel(track, get(), get(), get{ parametersOf(context) })
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

    viewModel { (context: Context) ->
        LibraryFragmentViewModel(get{ parametersOf(context) })
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel { (context: Context) ->
        PlaylistCreateViewModel(get{ parametersOf(context) })
    }

    viewModel{ (playlist: Playlist, context: Context) ->
        PlaylistViewModel(playlist,get{ parametersOf(context) })
    }

    viewModel{ (playlist: Playlist, context:Context) ->
        PlaylistEditViewModel(playlist, get{ parametersOf(context) })
    }
}