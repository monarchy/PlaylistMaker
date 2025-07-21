package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class FavoriteTracksEntity(
    @PrimaryKey
    val trackId:Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate:String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val addedDate: Long = System.currentTimeMillis()
)
