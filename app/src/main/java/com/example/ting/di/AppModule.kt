package com.example.ting.di

import android.content.Context
import androidx.room.Room
import com.example.ting.db.AppDatabase
import com.example.ting.other.Constants.APP_DATABASE
import com.example.ting.other.Constants.BASE_URL
import com.example.ting.remote.RecommendService
import com.retrofit2.converter.JsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE).build()

    @Singleton
    @Provides
    fun provideRecommendService(): RecommendService =
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(JsonConverterFactory.create(Json {
                ignoreUnknownKeys = true
            }))
            .build()
            .create(RecommendService::class.java)
}