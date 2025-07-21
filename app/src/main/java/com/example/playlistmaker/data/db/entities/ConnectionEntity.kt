package com.example.playlistmaker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "connection_table",
    primaryKeys = ["trackId", "playlistId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TracksEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("playlistId"),
        Index("trackId")
    ]
)
data class ConnectionEntity(
    val trackId: Long,
    val playlistId: Long,
    val addedDate: Long = System.currentTimeMillis()
)
