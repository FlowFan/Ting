package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class NewSong(
    val result: List<Result> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = "",
        val song: Song = Song()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Song(
            val artists: List<Artist> = listOf()
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class Artist(
                val name: String = ""
            ) : Parcelable
        }
    }
}