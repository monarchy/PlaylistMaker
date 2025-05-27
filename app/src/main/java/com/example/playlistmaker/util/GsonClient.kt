package com.example.playlistmaker.util

import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonClient {

    private val gson = Gson()

    fun listToJson(tracks: MutableList<Track>): String {
        return gson.toJson(tracks)
    }

    fun objectToJson(track: Track): String {
        return gson.toJson(track)
    }

    fun arrayFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun fromJsonToPlayer(json: String): Track {
        var track: Track = gson.fromJson(json, object : TypeToken<Track>() {}.type)
        val newArtWorkUrl = track.artworkUrl100.replaceAfterLast('/', FORMAT_SIZE)
        track.artworkUrl100 = newArtWorkUrl
        return track
    }

    private const val FORMAT_SIZE = "512x512bb.jpg"
}