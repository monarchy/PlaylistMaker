package com.example.playlistmaker.data.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.playlistmaker.data.db.entities.ConnectionEntity
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TracksEntity

data class PlaylistWithTracksDb(
    @Embedded
    val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(
            ConnectionEntity::class,
            parentColumn = "playlistId",
            entityColumn = "trackId"
        )
    )
    val tracks: List<TracksEntity>  // Итоговый список треков
)