package com.example.playlistmaker.ui.media_library.favorite

import com.example.playlistmaker.domain.models.Track

sealed class FavoriteState(){
    data object Empty:FavoriteState()
    data class Content(val favoriteList: List<Track>):FavoriteState()
}
