package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserPlaylist(
    val playlist: List<Playlist> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Playlist(
        val id: Long = 0,
        val coverImgUrl: String = "",
        val creator: Creators = Creators(),
        val name: String = "",
        val playCount: Long = 0,
        val trackCount: Int = 0
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Creators(
            val userId: Long = 0
        ) : Parcelable
    }
}
