package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.ConnectionEntity

@Dao
interface ConnectionTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylistLink(connectionEntity: ConnectionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addTrackToConnectionTable(connectionEntity: ConnectionEntity)

    @Query("DELETE FROM connection_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) //ИСПОЛЬЗУЕТСЯ

    @Query("SELECT EXISTS(SELECT * FROM connection_table WHERE trackId = :trackId LIMIT 1)")
    suspend fun findTrackInConnectionTable(trackId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM connection_table WHERE trackId =:trackId)")
    suspend fun isTrackInConnectionTable(trackId: Long): Boolean //ИСПОЛЬЗУЕТСЯ

    @Query("SELECT trackId FROM connection_table WHERE playlistId = :playlistId")
    suspend fun getTracksIdFromPlaylist(playlistId: Long): List<Long> //ИСПОЛЬЗУЕТСЯ

    @Query("DELETE FROM connection_table WHERE playlistId=:playlistId")
    suspend fun deletePlaylistFromConectionTable(playlistId: Long)

    @Query("SELECT COUNT (*) FROM connection_table WHERE playlistId = :playlistId")
    suspend fun getTracksCountInPlaylist(playlistId: Long): Long

    @Query("SELECT * FROM connection_table WHERE playlistId = :playlistId")
    suspend fun getTracksFromPlaylist(playlistId: Long):List<ConnectionEntity>
}