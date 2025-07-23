package com.example.playlistmaker.ui.adapters.playlists_library

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistListViewHolder(itemView: View, val onTrackClickListener: (Playlist) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.posterPlaylist)
    private val playlistInfo: TextView = itemView.findViewById(R.id.containerText)
    fun bind(playlist: Playlist, context: Context) {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTrackClickListener(playlist)
            }
        }
        playlistInfo.text = textFormat(playlist, context)
        Glide.with(playlistPoster)
            .load(playlist.imagePath?.toUri())
            .centerCrop()
            .placeholder(R.drawable.placeholder_for_playlist)
            .error(R.drawable.placeholder_for_playlist)
            .into(playlistPoster)
    }

    private fun textFormat(playlist: Playlist, context: Context): String {
        val tracksCount = context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount.toInt(),
            playlist.tracksCount.toInt()
        )
        return "${playlist.playlistName}\n$tracksCount"
    }
}