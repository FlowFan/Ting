package com.example.ting.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.WAKE_MODE_LOCAL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import com.example.ting.repository.TingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        mediaSourceFactory: MediaSource.Factory
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        .setMediaSourceFactory(mediaSourceFactory)
        .setWakeMode(WAKE_MODE_LOCAL)
        .build()

    @OptIn(UnstableApi::class)
    @ServiceScoped
    @Provides
    fun provideMediaSourceFactory(
        @ApplicationContext context: Context,
        repository: TingRepository
    ): MediaSource.Factory = DefaultMediaSourceFactory(
        ResolvingDataSource.Factory(
            DefaultDataSource.Factory(context),
        ) {
            it.withUri(repository.resolveUri(it.uri))
        },
        DefaultExtractorsFactory()
    )
}