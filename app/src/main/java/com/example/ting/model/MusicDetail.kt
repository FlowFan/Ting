package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MusicDetail(
    val songs: List<Song> = listOf(Song())
) : Parcelable {
    @Parcelize
    @Serializable
    data class Song(
        val id: Long = 0,
        val al: Al = Al()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class Al(
            val picUrl: String = ""
        ) : Parcelable
    }
}