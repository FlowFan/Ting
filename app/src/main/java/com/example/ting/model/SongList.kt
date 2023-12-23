package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class SongList(
    val playlist: Playlist = Playlist()
) {
    @Serializable
    data class Playlist(
        val id: Long = 0,
        val coverImgUrl: String = "",
        val creator: Creators = Creators(),
        val description: String = "",
        val name: String = "",
        val subscribed: Boolean = false,
        val trackCount: Int = 0,
        val tracks: List<Track> = listOf()
    ) {
        @Serializable
        data class Creators(
            val nickname: String = ""
        )

        @Serializable
        data class Track(
            val id: Long = 0,
            val al: Al = Al(),
            val ar: List<Ar> = listOf(),
            val name: String = ""
        ) {
            @Serializable
            data class Al(
                val name: String = "",
                val picUrl: String = ""
            )

            @Serializable
            data class Ar(
                val name: String = ""
            )
        }
    }
}