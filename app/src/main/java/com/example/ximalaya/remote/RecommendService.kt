package com.example.ximalaya.remote

import com.example.ximalaya.model.Album
import retrofit2.http.GET

interface RecommendService {
    @GET("/v2/albums/guess_like?access_token=6e454a9dc26d9c6211028506f08c609f&app_key=9f9ef8f10bebeaa83e71e62f935bede8&client_os_type=2&device_id=be86e3fc5152f87b&device_id_type=Android_ID&device_type=2&like_count=10&pack_id=com.app.test.android&sdk_client_type=2&sdk_version=v8.0.7&sig=a0c746df740c613d503a72a9d0200bf0")
    suspend fun searchRecommendData(): List<Album>
}