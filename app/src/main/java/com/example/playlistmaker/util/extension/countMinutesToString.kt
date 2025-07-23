package com.example.playlistmaker.util.extension

import android.content.Context
import com.example.playlistmaker.R

fun Long.minutesToString(context: Context): String =
    context.resources.getQuantityString(
        R.plurals.minutes_count,
        this.toInt(),
        this.toInt()
    )