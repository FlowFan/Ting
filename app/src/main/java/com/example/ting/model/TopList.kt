package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class TopList(
    val list: List<Result> = listOf()
) {
    @Serializable
    data class Result(
        val id: Long = 0,
        val coverImgUrl: String = "",
        val name: String = ""
    )
}