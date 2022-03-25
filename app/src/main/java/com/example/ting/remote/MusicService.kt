package com.example.ting.remote

import com.example.ting.model.NewSong
import com.example.ting.model.PlayList
import com.example.ting.model.SongList
import com.example.ting.model.TopList
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MusicWeService {
    @POST("/weapi/personalized/playlist")
    @FormUrlEncoded
    suspend fun getPlayList(
        @FieldMap body: Map<String, String>
    ): PlayList

    @POST("/weapi/personalized/newsong")
    @FormUrlEncoded
    suspend fun getNewSong(
        @FieldMap body: Map<String, String>
    ): NewSong

    @GET("/api/toplist")
    suspend fun getTopList(): TopList

    @POST("/api/v6/playlist/detail")
    @FormUrlEncoded
    suspend fun getSongList(
        @FieldMap body: Map<String, String>
    ): SongList
}