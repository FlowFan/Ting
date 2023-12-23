package com.example.ting.remote

import com.example.ting.model.Album
import com.example.ting.model.Detail
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RecommendService {
    @GET("/v2/albums/guess_like")
    suspend fun searchRecommendData(
        @Query("app_key") appKey: String,
        @Query("client_os_type") clientOsType: Int,
        @Query("device_id") deviceId: String,
        @Query("device_id_type") deviceIdType: String,
        @Query("device_type") deviceType: Int,
        @Query("like_count") likeCount: Int,
        @Query("pack_id") packId: String,
        @Query("sdk_client_type") sdkClientType: Int,
        @Query("sdk_version") sdkVersion: String,
        @Query("access_token") accessToken: String,
        @Query("sig") sig: String
    ): List<Album>

    @GET("/albums/browse")
    suspend fun searchDetailData(
        @QueryMap queryMap: Map<String, @JvmSuppressWildcards Any>
    ): Detail
}