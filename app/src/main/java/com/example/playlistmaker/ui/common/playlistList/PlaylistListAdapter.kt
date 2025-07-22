package com.example.playlistmaker.ui.common.playlistList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistContainerBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistListAdapter(
    private val context: Context,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistListAdapter.PlaylistListViewHolder>() {

    var playlists: List<Playlist> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistListViewHolder {
        val binding = PlaylistContainerBinding.inflate(LayoutInflater.from(context), parent, false)
        return PlaylistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistListViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClick)
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistListViewHolder(private val binding: PlaylistContainerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val playlistPoster: ImageView = binding.posterPlaylist
        private val playlistInfo: TextView = binding.containerText
        private val TAG = "PlaylistListViewHolder"

        fun bind(playlist: Playlist, onItemClick: (Playlist) -> Unit) {
            Log.d(TAG, "Binding playlist: ${playlist.playlistName}")
            playlistInfo.text = textFormat(playlist, binding.root.context)
            Glide.with(playlistPoster)
                .load(playlist.imagePath?.toUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder_for_playlist)
                .error(R.drawable.placeholder_for_playlist)
                .into(playlistPoster)
            binding.root.setOnClickListener {
                Log.d(TAG, "Click detected on playlist: ${playlist.playlistName}")
                onItemClick(playlist)
            }
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
}