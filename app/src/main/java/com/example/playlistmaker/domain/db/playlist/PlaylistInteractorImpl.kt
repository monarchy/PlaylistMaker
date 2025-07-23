package com.example.playlistmaker.domain.db.playlist

import com.example.playlistmaker.domain.image_storage.ImageStorageRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.shairing.playlist.PlaylistSharingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistInteractorImpl(
    private val imageStorage: ImageStorageRepository,
    private val playlistDb: PlaylistDbRepository,
    private val playlistSharingRepository: PlaylistSharingRepository
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

    override fun getPlaylist(playlistId: Long): Flow<Playlist> = playlistDb.getPlaylist(playlistId)

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlistDb.getAllPlaylists()

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?> =
        playlistDb.getPlaylistWithTracks(playlistId)

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    ): Flow<Result<String>> = playlistDb.addTrackToPlaylist(track, playlist)

    override suspend fun deleteTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ) {
        playlistDb.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override fun sharePlaylist(playlistId: Long): Flow<Result<String>> = flow {
        val playlistToShare = playlistDb.getPlaylistWithTracks(playlistId).first()

        if (playlistToShare!!.trackList.isEmpty()) {
            throw IllegalArgumentException(TRACK_LIST_IS_EMPTY)
        }

        val formattedText = formatPlaylistForSharing(playlistToShare)
        playlistSharingRepository.sharingPlaylist(formattedText)
        emit(Result.success(SHARING_SUCCESS))
    }.catch { e ->
        emit(Result.failure(e))
    }


    override suspend fun updatePlaylistInfo(playlist: Playlist) {
        val oldPlaylist = playlistDb.getPlaylist(playlist.playlistId).first()

        val newImagePath = if (playlist.imagePath != null) {
            if (oldPlaylist.imagePath != playlist.imagePath) {
                oldPlaylist.imagePath?.let { imageStorage.deleteFromStorage(it) }
                imageStorage.uploadImage(playlist.imagePath)
            } else {
                oldPlaylist.imagePath
            }
        } else {
            oldPlaylist.imagePath?.let { imageStorage.deleteFromStorage(it) }
            null
        }

        val updatedPlaylist = playlist.copy(imagePath = newImagePath)
        playlistDb.updatePlaylistInfo(updatedPlaylist)
    }

    private fun formatPlaylistForSharing(playlist: PlaylistWithTracks): String {
        return buildString {
            append("Название плейлиста: ${playlist.playlistInfo.playlistName}\n")
            append(
                if (playlist.playlistInfo.playlistTitle.isNullOrEmpty()) "Описание отсутствует\n"
                else "Описание: ${playlist.playlistInfo.playlistTitle}\n"
            )
            append("${playlist.playlistInfo.tracksCount} треков\n\n")

            playlist.trackList.forEachIndexed { index, track ->
                append(
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${
                        formatTrackDuration(
                            track.trackTimeMillis
                        )
                    })\n"
                )
            }
        }
    }

    private fun formatTrackDuration(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).run {
            format(Date(millis))
        }
    }

    companion object {
        private const val TRACK_LIST_IS_EMPTY =
            "В этом плейлисте нет списка треков, которым можно поделиться"
        private const val UNKNOWN_ERROR = "Неизвестная ошибка, повторите снова"
        private const val SHARING_SUCCESS = ""
    }
}