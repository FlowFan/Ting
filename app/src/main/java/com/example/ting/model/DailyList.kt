package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyList(
    val data: Data = Data()
) {
    @Serializable
    data class Data(
        val dailySongs: List<DailySong> = listOf()
    ) {
        @Serializable
        data class DailySong(
            val id: Long = 0,
            val al: Al = Al(),
            val ar: List<Ar> = listOf(),
            val name: String = ""
        ) {
            @Serializable
            data class Al(
                val name: String = "",
                val picUrl: String = ""
            )

            @Serializable
            data class Ar(
                val name: String = ""
            )
        }
    }
}