package com.example.ting.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class DailyWord(
    @PrimaryKey(true)
    @Transient
    val id: Int = 0,
    val hitokoto: String = "",
    val from: String = ""
)