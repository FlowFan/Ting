package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeList(
    val ids: List<Long> = listOf()
)