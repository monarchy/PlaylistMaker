package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val onItemClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: ArrayList<Track> = ArrayList()

    fun updateTracks(tracks: List<Track>) {
        requireNotNull(tracks) { "New tracks list is null" }
        this.tracks.clear()
        this.tracks.addAll(tracks)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_maket, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickListener(track)
        }
    }
}
