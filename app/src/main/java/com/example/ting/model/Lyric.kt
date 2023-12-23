package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class Lyric(
    val lrc: Lrc = Lrc(),
    val klyric: Lrc = Lrc(),
    val tlyric: Lrc = Lrc()
) {
    @Serializable
    data class Lrc(
        val lyric: String = ""
    )
}