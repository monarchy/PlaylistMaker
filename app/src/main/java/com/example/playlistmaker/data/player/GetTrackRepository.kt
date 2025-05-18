package com.example.playlistmaker.data.player

import com.example.playlistmaker.domain.models.Track

interface GetTrackRepository {
    fun get(string: String):Track
}