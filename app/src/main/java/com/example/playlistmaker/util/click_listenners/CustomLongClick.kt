package com.example.playlistmaker.util.click_listenners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> longTrackClick(
    coroutineScope: CoroutineScope,
    action: (T) -> Unit
): (T) -> Unit {
    return { param: T ->
        coroutineScope.launch {
            action(param)
        }
    }
}