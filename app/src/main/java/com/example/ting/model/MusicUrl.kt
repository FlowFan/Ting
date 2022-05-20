package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MusicUrl(
    val data: List<Data> = listOf()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Data(
        val url: String = ""
    ) : Parcelable
}