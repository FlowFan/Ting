package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class NewSong(
    val result: List<Result> = listOf()
) {
    @Serializable
    data class Result(
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = "",
        val song: Song = Song()
    ) {
        @Serializable
        data class Song(
            val artists: List<Artist> = listOf()
        ) {
            @Serializable
            data class Artist(
                val name: String = ""
            )
        }
    }
}