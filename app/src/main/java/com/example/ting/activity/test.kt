package com.example.ting.activity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 *
 * @author fanshun
 * @date 2024/9/23 18:59
 */
fun main() {
    val list = listOf(1, 2, 3, 4, 5)

    list.zipWithNext { a, b ->
        println("Current: $b, Previous: $a")
    }
    val json = """
        {
          "code": 0
        }
    """.trimIndent()
    println(
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }.decodeFromString<Response<Song>>(json)
    )
}

@Serializable
data class Response<T>(
    val code: Int,
    val message: String = "",
    val data: T
)

@Serializable
data class Song(
    val songId: Long = 0
)