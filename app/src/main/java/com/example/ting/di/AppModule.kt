package com.example.ting.di

import android.content.Context
import androidx.room.Room
import com.example.ting.db.AppDatabase
import com.example.ting.other.Constants.APP_DATABASE
import com.example.ting.other.Constants.BASE_URL
import com.example.ting.other.CookieHelper
import com.example.ting.other.HttpsInterceptor
import com.example.ting.other.UserAgentInterceptor
import com.example.ting.remote.HitokotoService
import com.example.ting.remote.MusicWeService
import com.example.ting.remote.RecommendService
import com.example.ting.remote.UrlService
import com.retrofit2.converter.JsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
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
    fun provideRecommendService() =
        Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(JsonConverterFactory.create())
            .build()
            .create<RecommendService>()

    @Singleton
    @Provides
    fun provideHttpClient() =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(HttpsInterceptor())
            .addInterceptor(UserAgentInterceptor())
            .cookieJar(CookieHelper())
            .build()

    @Singleton
    @Provides
    fun provideMusicWeService(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://music.163.com")
            .addConverterFactory(JsonConverterFactory.create())
            .build()
            .create<MusicWeService>()

    @Singleton
    @Provides
    fun provideHitokotoService(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://v1.hitokoto.cn")
            .addConverterFactory(JsonConverterFactory.create())
            .build()
            .create<HitokotoService>()

    @Singleton
    @Provides
    fun provideUrlService(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://interface3.music.163.com")
            .addConverterFactory(JsonConverterFactory.create())
            .build()
            .create<UrlService>()
}