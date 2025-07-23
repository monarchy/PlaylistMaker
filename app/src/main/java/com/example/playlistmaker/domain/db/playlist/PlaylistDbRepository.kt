package com.example.playlistmaker.domain.db.playlist

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistDbRepository {
    fun createPlaylist(playlist: Playlist): Flow<Result<String>>
    suspend fun deletePlaylist(playlistId: Long)
    fun getPlaylist(playlistId: Long): Flow<Playlist>
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>
    fun addTrackToPlaylist(track: Track, playlist: Playlist): Flow<Result<String>>
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun updatePlaylistInfo(playlist: Playlist)
}