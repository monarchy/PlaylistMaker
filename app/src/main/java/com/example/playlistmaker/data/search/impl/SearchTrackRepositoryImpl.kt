package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchTrackRepository
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SearchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTrackRepositoryImpl(private val networkClient: NetworkClient) : SearchTrackRepository {
    override fun searchTracks(expression: String): List<Track> {
        var response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200 && response is TrackSearchResponse) {
            return response.results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
}

