package com.example.playlistmaker.ui.common.playlistList

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistListViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val playlistPoster: ImageView = itemView.findViewById(R.id.posterPlaylist)
    private val playlistInfo: TextView = itemView.findViewById(R.id.containerText)
    fun bind(playlist: Playlist, context: Context) {
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
            R.plurals.items_count,
            playlist.tracksCount.toInt(),
            playlist.tracksCount.toInt()
        )
        return "${playlist.playlistName}\n$tracksCount"
    }
}