package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.trackName)
        val artistName: TextView = itemView.findViewById(R.id.artistName)
        val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_maket, parent, false)
        return TrackViewHolder(view)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.trackName.text = track.trackName
        holder.artistName.text = track.artistName
        holder.trackTime.text = track.trackTime

        Glide.with(holder.itemView.context)
            .load(track.artworkUrl100)
            .into(holder.trackImage)
    }

    override fun getItemCount(): Int = tracks.size
}
