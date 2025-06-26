package com.example.playlistmaker.data.network

import android.content.Context
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.util.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val tunesService: ITunesApi,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            if (context.isInternetAvailable()) {
                tunesService.searchTracks(dto as TrackSearchRequest)
            } else {
                throw NoConnectionException()
            }
        }
    }
}