package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SongList(
    val playlist: Playlist = Playlist()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Playlist(
        val coverImgUrl: String = "",
        val creator: Creators = Creators(),
        val description: String = "",
        val id: Long = 0,
        val name: String = "",
        val subscribed: Boolean = false,
        val trackCount: Int = 0,
        val tracks: List<Track> = listOf()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Creators(
            val nickname: String = ""
        ) : Parcelable

        @Parcelize
        @Serializable
        data class Track(
            val id: Long = 0,
            val al: Al = Al(),
            val ar: List<Ar> = listOf(),
            val name: String = ""
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class Al(
                val name: String = "",
                val picUrl: String = ""
            ) : Parcelable

            @Parcelize
            @Serializable
            data class Ar(
                val name: String = ""
            ) : Parcelable
        }
    }
}