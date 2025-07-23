package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.PlaylistWithTracksDb
import com.example.playlistmaker.data.db.entities.ConnectionEntity
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToTrackTable(track: TracksEntity)

    @Query("SELECT * FROM track_table")
    fun getAllTracks(): Flow<List<TracksEntity>>

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY addedDate DESC")
    fun getAllFavoriteTracks(): Flow<List<TracksEntity>>

    @Query("SELECT trackId FROM track_table WHERE isFavorite = 1 ORDER BY addedDate DESC")
    fun getAllFavoriteTracksId(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId AND isFavorite = 1)")
    suspend fun isFavoriteTrack(trackId: Long): Boolean //ИСПОЛЬЗУЕТСЯ

    @Query("SELECT trackId FROM track_table")
    fun getAllTrackId(): Flow<List<Long>>

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrackFromTable(trackId: Long) //ИСПОЛЬЗУЕТСЯ

    @Query("SELECT * FROM track_table WHERE trackId = :trackId AND isFavorite = 1")
    suspend fun getFavoriteTrack(trackId: Long): TracksEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId=:trackId)")
    suspend fun isContainsInTrackTable(trackId: Long): Boolean //ИСПОЛЬЗУЕТСЯ
}
