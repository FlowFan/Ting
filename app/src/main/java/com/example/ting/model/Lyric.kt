package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Lyric(
    val lrc: Lrc = Lrc(),
    val tlyric: Tlyric = Tlyric()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Lrc(
        val lyric: String = ""
    ) : Parcelable

    @Parcelize
    @Serializable
    data class Tlyric(
        val lyric: String = ""
    ) : Parcelable
}

data class LyricLine(
    val time: Int,
    val lyric: String,
    var translation: String?
)

fun Lyric.parse(): List<LyricLine> {
    val lines = lrc.lyric.split("\n")
        .filter {
            it.matches(Regex("\\[\\d+:\\d+.\\d+].+"))
        }.map {
            val minutes = it.substring(1 until (it.indexOf(":"))).toIntOrNull() ?: 0
            val seconds =
                it.substring((it.indexOf(":") + 1) until it.indexOf(".")).toIntOrNull() ?: 0
            val time = minutes * 60 + seconds
            LyricLine(
                time = time,
                lyric = it.substring(it.indexOf("]") + 1),
                translation = null
            )
        }

    // 将翻译添加到歌词中
    if (tlyric.lyric.isNotBlank()) {
        tlyric.lyric.split("\n").filter {
            it.matches(Regex("\\[\\d+:\\d+.\\d+].+"))
        }.forEach {
            val minutes = it.substring(1 until (it.indexOf(":"))).toIntOrNull() ?: 0
            val seconds =
                it.substring((it.indexOf(":") + 1) until it.indexOf(".")).toIntOrNull() ?: 0
            val time = minutes * 60 + seconds
            lines.find { lyric -> lyric.time == time && lyric.translation == null }?.translation =
                it.substring(it.indexOf("]") + 1)
        }
    }
    return lines
}

fun Long.formatAsPlayerTime(): String {
    val minutes = String.format("%02d", this / 60_000L)
    val seconds = String.format("%02d", (this % 60_000L) / 1000L)
    return "$minutes:$seconds"
}