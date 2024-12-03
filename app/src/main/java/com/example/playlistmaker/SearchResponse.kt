package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class SearchResponse(val resultCount: Int,
                          @SerializedName("results") val results: List<Track>)
