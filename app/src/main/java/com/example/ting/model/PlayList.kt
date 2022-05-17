package com.example.ting.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlayList(
    val result: List<Result> = listOf()
) : Parcelable {
    @Entity(tableName = "PlayList.Result")
    @Parcelize
    @Serializable
    data class Result(
        @PrimaryKey
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = ""
    ) : Parcelable
}