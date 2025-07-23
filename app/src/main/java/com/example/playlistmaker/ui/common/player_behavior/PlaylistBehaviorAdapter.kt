package com.example.playlistmaker.ui.common.player_behavior

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistBehaviorAdapter(
    private val context: Context,
    private val onTrackClickListener: (Playlist) -> Unit
) :
    RecyclerView.Adapter<PlaylistBehaviorViewHolder>() {

    var playlists: List<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBehaviorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_bottom_layout, parent, false)
        return PlaylistBehaviorViewHolder(view, onTrackClickListener)
    }

    override fun onBindViewHolder(holder: PlaylistBehaviorViewHolder, position: Int) {
        holder.bind(
            playlists[position],
            context = context
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}