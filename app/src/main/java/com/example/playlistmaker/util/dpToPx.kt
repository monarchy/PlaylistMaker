package com.example.playlistmaker.util

import android.content.Context

fun Int.dpToPx(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()