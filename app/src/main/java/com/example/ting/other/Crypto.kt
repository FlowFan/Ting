package com.example.ting.other

import korlibs.crypto.AES
import korlibs.crypto.Padding
import korlibs.crypto.md5
import korlibs.encoding.base64
import korlibs.encoding.hex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val presetKey = "0CoJUm6Qyw8W8jud"
private const val iv = "0102030405060708"
private const val pubKey = "010001"
private const val modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7"
private const val eapiKey = "e82ckenh8dichen8"

fun Map<String, Any>.encryptWeAPI(): Map<String, String> {
    val rawJson = Json.encodeToString(mapValues { it.value.toString() })

    val key = buildString {
        repeat(16) {
            append((('a'..'z') + ('A'..'Z') + ('0'..'9')).random())
        }
    }

    return mapOf(
        "params" to AES.encryptAesCbc(
            data = AES.encryptAesCbc(
                data = rawJson.toByteArray(),
                key = presetKey.toByteArray(),
                iv = iv.toByteArray(),
                padding = Padding.PKCS7Padding
            ).base64.toByteArray(),
            key = key.toByteArray(),
            iv = iv.toByteArray(),
            padding = Padding.PKCS7Padding
        ).base64,
        "encSecKey" to key.reversed().toByteArray().hex.toBigInteger(16).modPow(
            pubKey.toBigInteger(16),
            modulus.toBigInteger(16)
        ).toString(16).padStart(256, '0')
    )
}

fun Map<String, Any>.encryptEApi(): Map<String, String> {
    val rawJson = Json.encodeToString(mapValues { it.value.toString() })
    val url = "/api/song/enhance/player/url"
    val message = "nobody" + url + "use" + rawJson + "md5forencrypt"
    val digest: String = message.toByteArray().md5().hex

    return mapOf(
        "params" to AES.encryptAesEcb(
            data = "$url-36cd479b6b5-$rawJson-36cd479b6b5-$digest".toByteArray(),
            key = eapiKey.toByteArray(),
            padding = Padding.PKCS7Padding
        ).hex
    )
}