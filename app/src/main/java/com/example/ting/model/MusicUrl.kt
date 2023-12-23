package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicUrl(
    val data: List<Data> = listOf()
) {
    @Serializable
    data class Data(
        val url: String = ""
    )
}