package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class TypeList(
    val categories: Map<String, String> = mapOf(),
    val sub: List<Sub> = listOf()
) {
    @Serializable
    data class Sub(
        val category: Int = 0,
        val name: String = ""
    )
}

@Serializable
data class HotPlaylistTag(
    val tags: List<Tag> = listOf()
) {
    @Serializable
    data class Tag(
        val name: String = ""
    )
}

@Serializable
data class HighQualityPlaylist(
    val playlists: List<Playlists> = listOf()
)

@Serializable
data class Playlists(
    val id: Long = 0,
    val coverImgUrl: String = "",
    val name: String = ""
)

@Serializable
data class TopPlaylists(
    val more: Boolean = false,
    val playlists: List<Playlists> = listOf()
)