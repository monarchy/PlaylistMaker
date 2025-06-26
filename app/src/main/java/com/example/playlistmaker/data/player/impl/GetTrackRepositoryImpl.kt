package com.example.playlistmaker.data.player.impl

import com.example.playlistmaker.domain.player.GetTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.GsonClient

class GetTrackRepositoryImpl(private val gson: GsonClient) : GetTrackRepository {
    override fun get(string: String): Track {
        val formatedTrack = editCoverSize(string)
        return gson.fromJsonToPlayer(formatedTrack)
    }

    private fun editCoverSize(string: String): String {
        val regex = Regex("""\d+x\d+bb\.jpg""")
        return regex.replace(string, FORMAT_SIZE)
    }

    companion object {
        private const val FORMAT_SIZE = "512x512bb.jpg"
    }
}
