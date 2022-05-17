package com.example.ting.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity
@Parcelize
@Serializable
data class DailyWord(
    @PrimaryKey
    val id: Int = 0,
    val hitokoto: String = "",
    val from: String = ""
) : Parcelable
