package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.PlaylistWithTracksDb
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId=:playlistId")
    fun getPlaylistEntity(playlistId: Long): Flow<PlaylistEntity>

    @Query("DELETE FROM playlist_table WHERE playlistId=:playlistId")
    suspend fun deletePlaylistFromTable(playlistId: Long)

    @Update
    suspend fun updatePlaylistInfo(playlistEntity: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createPlaylist(playlistEntity: PlaylistEntity)

    @Query("UPDATE playlist_table SET tracksCount=:trackCount WHERE playlistId=:playlistId")
    suspend fun updateTracksCount(trackCount: Long, playlistId: Long)

    @Query("DELETE FROM connection_table WHERE playlistId = :playlistId")
    suspend fun deleteAllTracksFromPlaylist(playlistId: Long)

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracksDb?>
}