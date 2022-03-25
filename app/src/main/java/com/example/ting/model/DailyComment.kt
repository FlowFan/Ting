package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DailyWord(
    @SerialName("hitokoto")
    val hitokoto: String,
    @SerialName("from")
    val from: String
) : Parcelable
