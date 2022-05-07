package com.example.ting.remote

import com.example.ting.model.*
import kotlinx.serialization.json.JsonObject
import retrofit2.http.*

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

    @POST("/weapi/playlist/catalogue")
    @FormUrlEncoded
    suspend fun getTypeList(
        @FieldMap body: Map<String, String>
    ): TypeList

    @POST("/weapi/playlist/hottags")
    @FormUrlEncoded
    suspend fun getHotPlaylistTags(
        @FieldMap body: Map<String, String>
    ): HotPlaylistTag

    @POST("/api/playlist/highquality/list")
    @FormUrlEncoded
    suspend fun getHighQualityPlaylist(
        @FieldMap body: Map<String, String>
    ): HighQualityPlaylist

    @POST("/weapi/playlist/list")
    @FormUrlEncoded
    suspend fun getTopPlaylist(
        @FieldMap body: Map<String, String>
    ): TopPlaylists

    @POST("/weapi/login/cellphone")
    @FormUrlEncoded
    suspend fun loginCellphone(
        @FieldMap body: Map<String, String>
    ): LoginResponse

    @POST("/weapi/login/token/refresh")
    @FormUrlEncoded
    suspend fun refreshLogin(
        @FieldMap body: Map<String, String>
    ): JsonObject

    @GET("/api/nuser/account/get")
    suspend fun getAccountDetail(): AccountDetail

    @POST("/api/user/playlist")
    @FormUrlEncoded
    suspend fun getUserPlaylist(
        @FieldMap body: Map<String, String>
    ): UserPlaylist

    @POST("/weapi/playlist/{action}")
    @FormUrlEncoded
    suspend fun subPlaylist(
        @Path("action") action: String,
        @FieldMap body: Map<String, String>
    ): SubPlaylistResult
}