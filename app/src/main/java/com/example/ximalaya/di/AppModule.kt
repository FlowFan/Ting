package com.example.ximalaya.di

import com.example.ximalaya.other.Constants.BASE_URL
import com.example.ximalaya.remote.RecommendService
import com.example.ximalaya.model.Album
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(object : Converter.Factory() {
                private val json = Json {
                    ignoreUnknownKeys = true
                }

                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<out Annotation>,
                    retrofit: Retrofit
                ): Converter<ResponseBody, *> {
                    return Converter {
                        json.decodeFromString<List<Album>>(it.string())
                    }
                }
            })
            .build()

    @Singleton
    @Provides
    fun provideRecommendService(retrofit: Retrofit): RecommendService =
        retrofit.create(RecommendService::class.java)
}