package com.example.ximalaya.remote

import com.example.ximalaya.model.Album
import retrofit2.http.GET

interface RecommendService {
    @GET("/v2/albums/guess_like?access_token=d13c1aef7021696643adc208f818c7ee&app_key=9f9ef8f10bebeaa83e71e62f935bede8&client_os_type=2&device_id=be86e3fc5152f87b&device_id_type=Android_ID&device_type=2&like_count=10&pack_id=com.app.test.android&sdk_client_type=2&sdk_version=v8.0.7&sig=b928105408a5211d900a86fdf29a5609")
    suspend fun searchRecommendData(): List<Album>
}