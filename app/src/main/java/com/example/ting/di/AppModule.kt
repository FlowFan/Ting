package com.example.ting.di

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.room.Room
import com.example.ting.db.AppDatabase
import com.example.ting.exoplayer.MusicService
import com.example.ting.other.Constants.ACCESS_TOKEN
import com.example.ting.other.Constants.APP_DATABASE
import com.example.ting.other.Constants.DEVICE_ID
import com.example.ting.other.CookieHelper
import com.example.ting.other.HttpsInterceptor
import com.example.ting.other.UserAgentInterceptor
import com.example.ting.remote.HitokotoService
import com.example.ting.remote.MusicWeService
import com.example.ting.remote.RecommendService
import com.example.ting.remote.UrlService
import com.google.common.util.concurrent.ListenableFuture
import com.retrofit2.converter.JsonConverterFactory
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @SuppressLint("HardwareIds")
    @Singleton
    @Provides
    @Named(DEVICE_ID)
    fun provideDeviceId(@ApplicationContext context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    @Singleton
    @Provides
    @Named(ACCESS_TOKEN)
    fun provideAccessToken(): String =
        AccessTokenManager.getInstanse().accessToken

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher =
        Dispatchers.IO

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        APP_DATABASE
    ).build()

    @Singleton
    @Provides
    fun provideJsonConverterFactory(): JsonConverterFactory =
        JsonConverterFactory.create {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Singleton
    @Provides
    fun provideHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(HttpsInterceptor())
        .addInterceptor(UserAgentInterceptor(context))
        .cookieJar(CookieHelper(context))
        .build()

    @Singleton
    @Provides
    fun provideMusicWeService(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: JsonConverterFactory
    ): MusicWeService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://music.163.com/")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create()

    @Singleton
    @Provides
    fun provideUrlService(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: JsonConverterFactory
    ): UrlService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://interface3.music.163.com/")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create()

    @Singleton
    @Provides
    fun provideHitokotoService(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: JsonConverterFactory
    ): HitokotoService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://v1.hitokoto.cn/")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create()

    @Singleton
    @Provides
    fun provideRecommendService(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: JsonConverterFactory
    ): RecommendService = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.ximalaya.com/openapi-gateway-app/")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create()

    @Singleton
    @Provides
    fun provideSessionToken(
        @ApplicationContext context: Context
    ): SessionToken = SessionToken(
        context,
        ComponentName(
            context,
            MusicService::class.java
        )
    )

    @Singleton
    @Provides
    fun provideMediaController(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> =
        MediaController.Builder(
            context,
            sessionToken
        ).buildAsync()
}