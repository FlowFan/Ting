package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SongList(
    @SerialName("playlist")
    val playlist: Playlist = Playlist()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Playlist(
        @SerialName("coverImgUrl")
        val coverImgUrl: String = "",
        @SerialName("creator")
        val creator: Creators = Creators(),
        @SerialName("description")
        val description: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("subscribed")
        val subscribed: Boolean = false,
        @SerialName("trackCount")
        val trackCount: Int = 0,
        @SerialName("tracks")
        val tracks: List<Track> = listOf()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Creators(
            @SerialName("nickname")
            val nickname: String = ""
        ) : Parcelable

        @Parcelize
        @Serializable
        data class Track(
            @SerialName("al")
            val al: Al = Al(),
            @SerialName("ar")
            val ar: List<Ar> = listOf(),
            @SerialName("name")
            val name: String = ""
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class Al(
                @SerialName("name")
                val name: String = "",
                @SerialName("picUrl")
                val picUrl: String = ""
            ) : Parcelable

            @Parcelize
            @Serializable
            data class Ar(
                @SerialName("name")
                val name: String = ""
            ) : Parcelable
        }
    }
}