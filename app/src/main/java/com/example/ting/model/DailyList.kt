package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DailyList(
    val data: Data = Data()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Data(
        val dailySongs: List<DailySong> = listOf()
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class DailySong(
            val id: Long = 0,
            val al: Al = Al(),
            val ar: List<Ar> = listOf(),
            val name: String = ""
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class Al(
                val name: String = "",
                val picUrl: String = ""
            ) : Parcelable

            @Parcelize
            @Serializable
            data class Ar(
                val name: String = ""
            ) : Parcelable
        }
    }
}
