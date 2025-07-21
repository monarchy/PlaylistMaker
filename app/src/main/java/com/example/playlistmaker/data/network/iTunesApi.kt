package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("/search?entity=song")
    suspend fun search(@Query("term", encoded = false) text: String): TrackSearchResponse
}