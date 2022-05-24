package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TopList(
    val list: List<Result> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        val id: Long = 0,
        val coverImgUrl: String = "",
        val name: String = ""
    ) : Parcelable
}
