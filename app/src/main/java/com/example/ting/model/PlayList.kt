package com.example.ting.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class PlayList(
    val result: List<Result> = listOf()
) {
    @Entity(tableName = "PlayList.Result")
    @Serializable
    data class Result(
        @PrimaryKey
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = ""
    )
}