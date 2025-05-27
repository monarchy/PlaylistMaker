package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.models.Track

interface GetTrackRepository {
    fun get(string: String):Track
}