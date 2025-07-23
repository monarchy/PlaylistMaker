package com.example.playlistmaker.data.db.playlist

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.example.playlistmaker.R
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entities.ConnectionEntity
import com.example.playlistmaker.data.db.entities.TracksEntity
import com.example.playlistmaker.domain.db.playlist.PlaylistDbRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistWithTracks
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistDbRepositoryImpl(
    private val db: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val context: Context
) : PlaylistDbRepository {

    override fun createPlaylist(
        playlist: Playlist
    ): Flow<Result<String>> = flow {
        try {
            db.playlistDao().createPlaylist(playlistDbConverter.map(playlist))
            emit(Result.success(context.getString(R.string.playlist_created, playlist.playlistName)))
        } catch (e: SQLiteConstraintException) {
            emit(Result.failure(Exception(
                context.getString(
                    R.string.playlist_already_exists,
                    playlist.playlistName
                ))))
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        db.withTransaction {
            val tracksIdFromPlaylist: List<Long> =
                db.connectionTableDao().getTracksIdFromPlaylist(playlistId)
            if (tracksIdFromPlaylist.isNotEmpty()) {
                tracksIdFromPlaylist.forEach() { trackId ->
                    db.connectionTableDao().deleteTrackFromPlaylist(playlistId, trackId)
                    if (!db.connectionTableDao()
                            .isTrackInConnectionTable(trackId) && !db.trackDao()
                            .isFavoriteTrack(trackId)
                    ) db.trackDao().deleteTrackFromTable(
                        trackId
                    )
                }
                db.playlistDao().deletePlaylistFromTable(playlistId)
            } else db.playlistDao().deletePlaylistFromTable(playlistId)
        }
    }


    override fun getPlaylist(playlistId: Long): Flow<Playlist> =
        db.playlistDao().getPlaylistEntity(playlistId).map {
            playlistDbConverter.map(it)
        }

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        db.playlistDao().getAllPlaylists().map { playlistEntities ->
            playlistEntities.map {
                playlistDbConverter.map(it)
            }
        }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> =
        db.playlistDao().getPlaylistWithTracks(playlistId).map { playlistWithTracksDb ->
            playlistDbConverter.map(playlistWithTracksDb)
        }

    override fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ): Flow<Result<String>> = flow {
        val trackEntity = trackDbConverter.map(track)
        try {
            db.withTransaction {
                addTrackToPlaylistInternal(trackEntity, playlist.playlistId)
            }
            emit(Result.success(context.getString(R.string.added_to_playlist, playlist.playlistName)))
        } catch (e: SQLiteConstraintException) {
            emit(Result.failure(Exception(
                context.getString(
                    R.string.track_already_added,
                    playlist.playlistName
                ))))
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        db.withTransaction {
            db.connectionTableDao().deleteTrackFromPlaylist(playlistId, trackId)
            if (!db.connectionTableDao().isTrackInConnectionTable(trackId) && !db.trackDao()
                    .isFavoriteTrack(trackId)
            ) db.trackDao().deleteTrackFromTable(trackId)
        }
    }

    private suspend fun addTrackToPlaylistInternal(trackEntity: TracksEntity, playlistId: Long) {
        when {
            db.trackDao()
                .isContainsInTrackTable(trackEntity.trackId) -> db.connectionTableDao()
                .addTrackToConnectionTable(
                    ConnectionEntity(
                        trackEntity.trackId,
                        playlistId
                    )
                )

            else -> {
                db.trackDao().addTrackToTrackTable(trackEntity)
                db.connectionTableDao().addTrackToConnectionTable(
                    ConnectionEntity(
                        trackEntity.trackId,
                        playlistId
                    )
                )
            }
        }
        val tracksCount =
            db.connectionTableDao().getTracksCountInPlaylist(playlistId)
        db.playlistDao().updateTracksCount(tracksCount, playlistId)
    }
}