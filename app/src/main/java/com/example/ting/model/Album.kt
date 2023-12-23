package com.example.ting.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@[Entity Parcelize Serializable]
data class Album(
    @[PrimaryKey(true) Transient]
    val id: Int = 0,

    @SerialName("id")
    val albumId: Long = 0,

    @SerialName("album_title")
    val albumTitle: String = "",

    @SerialName("album_intro")
    val albumIntro: String = "",

    @SerialName("play_count")
    val playCount: Long = 0,

    @SerialName("include_track_count")
    val includeTrackCount: Long = 0,

    @SerialName("cover_url_large")
    val coverUrl: String = "",

    @Transient
    val page: Int = 0
) : Parcelable