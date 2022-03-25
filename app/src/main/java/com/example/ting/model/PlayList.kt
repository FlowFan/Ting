package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlayList(
    @SerialName("result")
    val result: List<Result>
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("picUrl")
        val picUrl: String
    ) : Parcelable
}