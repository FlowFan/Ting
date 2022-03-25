package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TopList(
    @SerialName("list")
    val list: List<Result> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        @SerialName("coverImgUrl")
        val coverImgUrl: String = "",
        @SerialName("name")
        val name: String = ""
    ) : Parcelable
}
