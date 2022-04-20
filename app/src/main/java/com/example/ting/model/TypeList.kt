package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TypeList(
    val categories: Map<String, String> = mapOf(),
    val sub: List<Sub> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Sub(
        val category: Int = 0,
        val name: String = ""
    ) : Parcelable
}

@Parcelize
@Serializable
data class HotPlaylistTag(
    val tags: List<Tag> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Tag(
        val name: String = ""
    ) : Parcelable
}

@Parcelize
@Serializable
data class HighQualityPlaylist(
    val playlists: List<Playlists> = listOf()
) : Parcelable

@Parcelize
@Serializable
data class Playlists(
    val coverImgUrl: String = "",
    val id: Long = 0,
    val name: String = ""
) : Parcelable

@Parcelize
@Serializable
data class TopPlaylists(
    val more: Boolean = false,
    val playlists: List<Playlists> = listOf()
) : Parcelable