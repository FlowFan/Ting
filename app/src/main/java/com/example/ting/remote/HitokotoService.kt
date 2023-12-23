package com.example.ting.remote

import com.example.ting.model.DailyWord
import retrofit2.http.GET

fun interface HitokotoService {
    @GET("/")
    suspend fun getDailyWord(): DailyWord
}