package com.example.ting.other

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun rememberMediaSessionPlayer(clazz: Class<out Any>): State<MediaController?> {
    val context = LocalContext.current
    val controller = remember(context) {
        mutableStateOf<MediaController?>(null)
    }
    DisposableEffect(context) {
        val builder = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, clazz))
        ).buildAsync()

        builder.addListener(
            {
                controller.value = builder.get()
            },
            MoreExecutors.directExecutor()
        )

        onDispose {
            MediaController.releaseFuture(builder)
        }
    }
    return controller
}

fun Context.asyncGetSessionPlayer(clazz: Class<out Any>, handler: (MediaController) -> Unit) {
    val controller = MediaController.Builder(
        this,
        SessionToken(this, ComponentName(this, clazz))
    ).buildAsync()

    controller.addListener(
        {
            handler(controller.get())
        },
        MoreExecutors.directExecutor()
    )
}

fun buildMediaItem(mediaId: String, buildScope: MediaItem.Builder.() -> Unit): MediaItem =
    MediaItem.Builder()
        .setMediaId(mediaId)
        .apply(buildScope)
        .build()

fun MediaItem.Builder.metadata(scope: MediaMetadata.Builder.() -> Unit) {
    setMediaMetadata(MediaMetadata.Builder().apply(scope).build())
}

@Composable
fun rememberCurrentMediaItem(player: Player?): MediaItem? {
    var mediaItemState by remember(player) {
        mutableStateOf(player?.currentMediaItem)
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItemState = mediaItem
            }
        }
        player?.addListener(listener)
        onDispose {
            player?.removeListener(listener)
        }
    }
    return mediaItemState
}

@Composable
fun rememberPlayProgress(player: Player?): Pair<Long, Long>? {
    return produceState(
        initialValue = player?.let {
            it.currentPosition to it.duration
        },
        key1 = player
    ) {
        while (isActive) {
            value = player?.let {
                if (it.currentMediaItem == null) {
                    0L to 1L
                } else {
                    it.currentPosition to it.duration.coerceAtLeast(1)
                }
            }
            delay(500)
        }
    }.value
}

@Composable
fun rememberPlayState(player: Player?): Boolean? {
    var isPlayingState by remember(player) {
        mutableStateOf(player?.isPlaying)
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                isPlayingState = isPlaying
            }
        }
        player?.addListener(listener)
        onDispose {
            player?.removeListener(listener)
        }
    }
    return isPlayingState
}