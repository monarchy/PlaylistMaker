package com.example.playlistmaker.domain.db.playlist

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist): Flow<Result<String>>
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylist(playlistId: Long): Flow<Playlist>
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Flow<Result<String>>
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)
    fun sharePlaylist(playlistId: Long):Flow<Result<String>>
    suspend fun updatePlaylistInfo(playlist: Playlist)
}