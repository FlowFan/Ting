package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPlaylist(
    val playlist: List<Playlist> = listOf()
) {
    @Serializable
    data class Playlist(
        val id: Long = 0,
        val coverImgUrl: String = "",
        val creator: Creators = Creators(),
        val name: String = "",
        val playCount: Long = 0,
        val trackCount: Int = 0
    ) {
        @Serializable
        data class Creators(
            val userId: Long = 0
        )
    }
}