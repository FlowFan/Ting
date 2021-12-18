package com.example.ximalaya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Album(
    val id: Int,
    @SerialName("album_title") val albumTitle: String,
    @SerialName("album_intro") val albumIntro: String,
    @SerialName("play_count") val playCount: Long,
    @SerialName("include_track_count") val includeTrackCount: Long,
    @SerialName("cover_url_large") val coverUrl: String
) : Parcelable
