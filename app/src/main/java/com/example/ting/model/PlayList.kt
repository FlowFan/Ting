package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlayList(
    val result: List<Result> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        val id: Long = 0,
        val name: String = "",
        val picUrl: String = ""
    ) : Parcelable
}