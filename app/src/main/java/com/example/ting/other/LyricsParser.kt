package com.example.ting.other

import com.example.ting.model.Lyric
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class LyricsModel(
    val lines: List<LyricsLineModel> = listOf(),
    val duration: Duration = Duration.ZERO,
    val startOfVerse: Duration = Duration.ZERO,
    val title: String = "",
    val artist: String = ""
)

data class LyricsLineModel(
    val tones: List<Tone> = listOf(),
    val translation: Tone? = null,
) {
    data class Tone(
        val begin: Duration = Duration.ZERO,
        val end: Duration = Duration.ZERO,
        val word: String = "",
        val pitch: Int = 0,
        val highlight: Boolean = false,
        val highlightOffset: Float = -1f,
        val highlightWidth: Float = -1f
    )

    fun getStartTime(): Duration =
        tones.firstOrNull()?.begin ?: Duration.ZERO

    fun getEndTime(): Duration =
        tones.lastOrNull()?.end ?: Duration.ZERO
}

private val regexLrcLine = "((\\[\\d{2}:\\d{2}\\.\\d{2,3}])+)(.*)".toRegex()
private val regexTime = "\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})]".toRegex()
private val regexYrcLine = "\\[(-?[\\d,，]+)](.*)".toRegex()

//private val regexYrcText = "\\(([\\d,，]+)\\)(.*)".toRegex()
//private val regexYrcText = "\\([^)]*\\)[^()]*".toRegex()
private val regexYrcText = "\\(([\\d,，]+)\\)(.*)".toRegex()

fun Lyric.parse() {
    if (yrc.lyric.isNotBlank()) {
//        yrc.lyric.lines().filter {
//            it.isNotBlank()
//        }.mapNotNull {
//            regexLrcLine.matchEntire(it.trim())?.destructured
//        }.flatMap { (times, _, text) ->
//            regexTime.findAll(times).map { timeMatcher ->
//                val (min, sec, mil) = timeMatcher.destructured
//                LyricsLineModel.Tone(
//                    begin = min.toLong().minutes + sec.toLong().seconds + mil.padEnd(3, '0').toLong().milliseconds,
//                    word = text
//                )
//            }
//        }.zipWithNextLast { a, b ->
//            if (a == b) {
//                a.copy(end = b.begin + 8765.milliseconds)
//            } else {
//                a.copy(end = b.begin)
//            }
//        }
        yrc.lyric.lines().filter {
            it.isNotBlank()
        }.mapNotNull {
            regexYrcLine.matchEntire(it.trim())?.destructured
        }.forEach { (times, text) ->
            println(times)
            println(text)
            println(regexYrcText.findAll(text).map { it.value }.toList().joinToString("=="))
        }
    } else if (lrc.lyric.isNotBlank()) {
        val translations = tlyric.lyric.takeIf {
            it.isNotBlank()
        }?.let {
            parseLrcLyrics(it)
        }
        parseLrcLyrics(lrc.lyric).map { line ->
            LyricsLineModel(
                tones = listOf(line),
                translation = translations?.find {
                    it.begin == line.begin
                }
            )
        }.takeIf {
            it.isNotEmpty()
        }?.let {
            LyricsModel(
                lines = it,
                duration = it.last().getEndTime(),
                startOfVerse = it.first().getStartTime()
            )
        }
    } else {
        LyricsModel()
    }.also {
        println(it?.lines?.joinToString("\n"))
    }
}

fun parseLrcLyrics(lyrics: String): List<LyricsLineModel.Tone> =
    lyrics.lines().filter {
        it.isNotBlank()
    }.mapNotNull {
        regexLrcLine.matchEntire(it.trim())?.destructured
    }.flatMap { (times, _, text) ->
        regexTime.findAll(times).map { timeMatcher ->
            val (min, sec, mil) = timeMatcher.destructured
            LyricsLineModel.Tone(
                begin = min.toLong().minutes + sec.toLong().seconds + mil.padEnd(3, '0').toLong().milliseconds,
                word = text
            )
        }
    }.zipWithNextLast { a, b ->
        if (a == b) {
            a.copy(end = b.begin + 8765.milliseconds)
        } else {
            a.copy(end = b.begin)
        }
    }