package com.example.playlistmaker.ui.common.trackList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackListAdapter(private val onTrackClickListener: (Track) -> Unit) :
    RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {

    var tracks: List<Track> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackListViewHolder(view, onTrackClickListener)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    class TrackListViewHolder(itemView: View, private val onTrackClickListener: (Track) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.executor)
        private val artwork: ImageView = itemView.findViewById(R.id.cover)
        private val duration: TextView = itemView.findViewById(R.id.duration)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            Glide.with(artwork)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .error(R.drawable.network_error)
                .centerCrop()
                .into(artwork)
            duration.text = formatDuration(track.trackTimeMillis)
            itemView.setOnClickListener { onTrackClickListener(track) }
        }

        private fun formatDuration(millis: Long): String {
            val minutes = millis / 1000 / 60
            val seconds = (millis / 1000) % 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }
}