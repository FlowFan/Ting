package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayList(
    val result: List<Result> = listOf()
) {
    @Serializable
    data class Result(
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = ""
    )
}