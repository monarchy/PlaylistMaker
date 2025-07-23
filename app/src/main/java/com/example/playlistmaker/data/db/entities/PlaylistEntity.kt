package com.example.playlistmaker.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "playlist_table",
    indices = [Index(value = ["playlistName"], unique = true)]
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long,
    val playlistName: String,
    val playlistTitle: String?,
    val imagePath: String?,
    val tracksCount: Long,
)