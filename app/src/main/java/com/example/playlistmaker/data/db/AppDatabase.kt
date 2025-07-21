package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(version = 1, entities = [FavoriteTracksEntity::class])
abstract class AppDatabase:RoomDatabase() {
    abstract fun trackDao():TrackDao
}