package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class NewSong(
    @SerialName("result")
    val result: List<Result> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        @SerialName("name")
        val name: String = "",
        @SerialName("picUrl")
        val picUrl: String = "",
        @SerialName("song")
        val song: Song = Song()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Song(
            @SerialName("artists")
            val artists: List<Artist> = listOf()
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class Artist(
                @SerialName("name")
                val name: String = ""
            ) : Parcelable
        }
    }
}
