package com.example.playlistmaker.domain.db.playlist

import com.example.playlistmaker.domain.image_storage.ImageStorageRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(
    private val imageStorage: ImageStorageRepository,
    private val playlistDb: PlaylistDbRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist): Flow<Result<String>> =
        flow {
            try {
                if (playlist.imagePath != null) {
                    emit(
                        playlistDb.createPlaylist(
                            playlist.copy(imagePath = imageStorage.uploadImage(playlist.imagePath))
                        ).first()
                    )
                } else {
                    emit(playlistDb.createPlaylist(playlist).first())
                }
            } catch (e: Exception) {
                emit(Result.failure(Exception("Неизвестная ошибка")))
            }
        }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDb.deletePlaylist(playlist.playlistId)
        if (playlist.imagePath != null) imageStorage.deleteFromStorage(playlist.imagePath)
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> = flow {}

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlistDb.getAllPlaylists()
        .map { playlistList ->
            playlistList.map { playlist ->
                playlist.copy(imagePath = playlist.imagePath?.let {
                    "file://${
                        imageStorage.getImage(
                            playlist.imagePath
                        )
                    }"
                })
            }
        }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> =
        playlistDb.getPlaylistWithTracks(playlistId)

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    ): Flow<Result<String>> = playlistDb.addTrackToPlaylist(track, playlist)
}