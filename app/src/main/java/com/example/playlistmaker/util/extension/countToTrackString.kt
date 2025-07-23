package com.example.playlistmaker.util.extension

import android.content.Context
import com.example.playlistmaker.R

fun Long.trackCountToString(context: Context): String =
    context.resources.getQuantityString(
        R.plurals.tracks_count,
        this.toInt(),
        this.toInt()
    )