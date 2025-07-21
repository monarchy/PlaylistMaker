package com.example.playlistmaker.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TrackListViewHolder(
    itemView: View,
    private val onTrackClickListener: (Track) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val executor: TextView = itemView.findViewById(R.id.artistName)
    private val duration: TextView = itemView.findViewById(R.id.trackTime)
    private val cover: ImageView = itemView.findViewById(R.id.trackImage)

    fun bind(track: Track) {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onTrackClickListener(track)
            }
        }
        trackName.text = track.trackName
        executor.text = track.artistName

        val totalSeconds = track.trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        duration.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(cover)
    }
}