package com.example.playlistmaker.data.sharing.playlist

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.domain.shairing.playlist.PlaylistSharingRepository

class PlaylistSharingRepositoryImpl(private val context: Context): PlaylistSharingRepository {
    override fun sharingPlaylist(playlistToSharing: String) {
        val sharePlaylist = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, playlistToSharing)
        }
        context.startActivity(sharePlaylist)
    }
}