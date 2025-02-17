package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(private var tracks: MutableList<Track>, private val onTrackClick: (Track) -> Unit) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    var isHistory: Boolean = false

    fun bindHistory(history: List<Track>) {
        isHistory = true
        tracks.clear()
        tracks.addAll(history)
        notifyDataSetChanged()
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)

        fun bind(song: Track) {

            trackName.text = song.trackName
            artistName.text = song.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(song.trackTimeMillis)

            if (!song.artworkUrl100.isNullOrEmpty()){
                Glide.with(itemView.context)
                    .load(song.artworkUrl100)
                    .placeholder(R.drawable.placeholder)
                    .into(trackImage)

            }
            else
            {
                Glide.with(itemView.context)
                    .load(R.drawable.placeholder)
                    .into(trackImage)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_maket, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    override fun getItemCount() = tracks.size

}
