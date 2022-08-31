package com.example.ting.exoplayer

import android.app.PendingIntent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.example.ting.other.Constants.TING_PROTOCOL
import com.example.ting.other.toHttps
import com.example.ting.repository.TingRepository
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class MusicService : MediaLibraryService() {
    @Inject
    lateinit var musicRepo: TingRepository
    private val lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var player: Player
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    // 自定义datasource
                    ResolvingDataSource.Factory(
                        DefaultDataSource.Factory(this),
                        MusicResolver()
                    ),
                    DefaultExtractorsFactory()
                ),
            )
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        player.repeatMode = Player.REPEAT_MODE_ALL

        mediaSession = MediaLibrarySession.Builder(this, player, LibrarySessionCallback())
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    packageManager.getLaunchIntentForPackage(packageName),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    override fun onDestroy() {
        lifecycleScope.cancel()

        player.release()
        mediaSession.release()

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }

    class LibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems = mediaItems.map {
                it.buildUpon()
                    .setUri(it.requestMetadata.mediaUri)
                    .build()
            }
            return Futures.immediateFuture(updatedMediaItems)
        }
    }

    inner class MusicResolver : ResolvingDataSource.Resolver {
        override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
            // 动态解析歌曲地址
            if (dataSpec.uri.scheme == TING_PROTOCOL && dataSpec.uri.host == "music") {
                val musicId = dataSpec.uri.getQueryParameter("id")?.toLong() ?: error("can't find music id")
                val url = runBlocking {
                    var musicUrl = ""
                    musicRepo.getMusicUrl(musicId).collect {
                        if (it.data.isNotEmpty() && musicUrl.isBlank()) {
                            musicUrl = it.data[0].url
                        }
                    }
                    musicUrl
                }
                return dataSpec.buildUpon()
                    .apply {
                        if (url.isNotBlank()) {
                            setUri(Uri.parse(url.toHttpUrl().toHttps()))
                        }
                    }
                    .build()
            }
            return dataSpec
        }
    }
}