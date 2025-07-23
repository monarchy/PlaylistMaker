package com.example.playlistmaker.ui.adapters.playlist_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class PlaylistTracksAdapter(
    private val onTrackClickListener: (Track) -> Unit,
    val onTrackLongClickListenner: (Track) -> Unit
) :
    RecyclerView.Adapter<PlaylistTracksViewHolder>() {

    var tracks: List<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return PlaylistTracksViewHolder(view, onTrackClickListener, onTrackLongClickListenner)
    }

    override fun onBindViewHolder(holder: PlaylistTracksViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}