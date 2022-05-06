package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DailyWord(
    val hitokoto: String = "",
    val from: String = ""
) : Parcelable
