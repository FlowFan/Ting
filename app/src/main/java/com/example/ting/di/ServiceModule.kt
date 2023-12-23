package com.example.ting.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.WAKE_MODE_LOCAL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import com.example.ting.other.Constants.DOMAIN
import com.example.ting.other.Constants.TING_PROTOCOL
import com.example.ting.repository.TingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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
        ) { dataSpec ->
            runBlocking {
                dataSpec.uri.takeIf {
                    it.scheme == TING_PROTOCOL && it.host == DOMAIN
                }?.getQueryParameter("id")?.let {
                    repository.getMusicUrl(it)
                        .first()
                        .data
                        .getOrNull(0)
                        ?.url
                        ?.toUri()
                        ?.buildUpon()
                        ?.scheme("https")
                        ?.build()
                }?.let(dataSpec::withUri) ?: dataSpec
            }
        },
        DefaultExtractorsFactory()
    )
}