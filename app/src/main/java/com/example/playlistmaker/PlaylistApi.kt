package com.example.playlistmaker
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistApi {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term")term: String): Call<SearchResponse>
}