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
        val coverImgUrl: String = "",
        val id: Long = 0,
        val name: String = ""
    ) : Parcelable
}
