package com.example.playlistmaker.ui.adapters.player_behavior

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistBehaviorViewHolder(itemView: View, val onTrackClickListener: (Playlist) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val playlistPoster: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_behavior_name)
    private val playlistTracksCount: TextView = itemView.findViewById(R.id.playlist_tracks_count)
    fun bind(playlist: Playlist, context: Context) {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTrackClickListener(playlist)
            }
        }
        playlistName.text = playlist.playlistName
        playlistTracksCount.text = context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount.toInt(),
            playlist.tracksCount.toInt()
        )
        Glide.with(playlistPoster)
            .load(playlist.imagePath?.toUri())
            .centerCrop()
            .placeholder(R.drawable.placeholder_for_playlist)
            .error(R.drawable.placeholder_for_playlist)
            .into(playlistPoster)
    }
}