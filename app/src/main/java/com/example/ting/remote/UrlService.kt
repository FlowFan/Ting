package com.example.ting.remote

import com.example.ting.model.MusicUrl
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

fun interface UrlService {
    @POST("/eapi/song/enhance/player/url")
    @FormUrlEncoded
    suspend fun getMusicUrl(
        @FieldMap body: Map<String, String>
    ): MusicUrl
}