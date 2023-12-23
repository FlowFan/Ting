package com.example.ting.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Detail(
    @SerialName("total_page")
    val totalPage: Long = 0,

    @SerialName("current_page")
    val currentPage: Long = 0,

    val tracks: List<Track> = listOf()
) {
    @Serializable
    data class Track(
        val id: Int = 0,

        @SerialName("track_title")
        val trackTitle: String = "",

        @SerialName("play_count")
        val playCount: Long = 0,

        @SerialName("cover_url_large")
        val coverUrl: String = "",

        @SerialName("download_url")
        val downloadUrl: String = ""
    )
}