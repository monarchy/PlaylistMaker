package com.example.playlistmaker.util

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object GsonClient {

    private val gson = GsonBuilder().serializeNulls().create()

    fun trackListToJson(tracks: List<Track>): String {
        return gson.toJson(tracks)
    }

    fun trackToJson(track: Track): String {
        return gson.toJson(track)
    }

    fun trackListFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun trackFromJson(json: String): Track {
        val type = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, type)
    }

    fun playlistToJson(playlist: Playlist): String {
        return gson.toJson(playlist)
    }

    fun playlistFromJson(json: String): Playlist {
        val type = object : TypeToken<Playlist>() {}.type
        return gson.fromJson(json, type)
    }
}