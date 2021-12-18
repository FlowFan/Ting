package com.example.ximalaya.remote

import com.example.ximalaya.model.Album
import retrofit2.http.GET

interface RecommendService {
    @GET("/v2/albums/guess_like?access_token=47c0de03160155ac17bf68ba27fb1ce0&app_key=9f9ef8f10bebeaa83e71e62f935bede8&client_os_type=2&device_id=500fb94023be3010&device_id_type=Android_ID&device_type=2&like_count=10&pack_id=com.app.test.android&sdk_client_type=2&sdk_version=v8.0.7&sig=81f6b1f9501926c3f7a1737bd24d9288")
    suspend fun searchRecommendData(): List<Album>
}