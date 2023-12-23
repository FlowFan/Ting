package com.example.ting.model

import androidx.media3.common.MediaItem

sealed interface Event {
    data class OnMediaItemTransition(val mediaItem: MediaItem, val reason: Int) : Event

    data class OnIsPlayingChanged(val isPlaying: Boolean) : Event
}