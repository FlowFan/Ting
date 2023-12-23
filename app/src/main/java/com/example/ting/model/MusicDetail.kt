package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicDetail(
    val songs: List<Song> = listOf()
) {
    @Serializable
    data class Song(
        val id: Long = 0,
        val al: Al = Al()
    ) {
        @Serializable
        data class Al(
            val picUrl: String = ""
        )
    }
}