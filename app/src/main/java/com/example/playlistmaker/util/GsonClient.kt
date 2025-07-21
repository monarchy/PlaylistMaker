package com.example.playlistmaker.util

import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonClient {

    private val gson = Gson()

    fun listToJson(tracks: List<Track>): String {
        return gson.toJson(tracks)
    }

    fun objectToJson(track: Track): String {
        return gson.toJson(track)
    }

    fun arrayFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun objectFromJson(json: String): Track {
        val type = object : TypeToken<Track>() {}.type
        return gson.fromJson(json,type)
    }
}