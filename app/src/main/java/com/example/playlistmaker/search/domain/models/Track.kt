package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) : Parcelable {

    fun getCoverArtwork(): String? = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

}