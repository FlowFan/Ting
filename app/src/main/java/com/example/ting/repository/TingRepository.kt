package com.example.ting.repository

import androidx.lifecycle.liveData
import androidx.media3.session.MediaController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.ting.db.AppDatabase
import com.example.ting.other.Constants.ACCESS_TOKEN
import com.example.ting.other.Constants.DEVICE_ID
import com.example.ting.other.Constants.PAGE_SIZE
import com.example.ting.other.encryptEApi
import com.example.ting.other.encryptWeAPI
import com.example.ting.remote.*
import com.google.common.util.concurrent.ListenableFuture
import korlibs.crypto.md5
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Named

class TingRepository @Inject constructor(
    private val recommendService: RecommendService,
    private val musicWeService: MusicWeService,
    private val urlService: UrlService,
    private val hitokotoService: HitokotoService,
    private val database: AppDatabase,
    @Named(DEVICE_ID) private val deviceId: String,
    @Named(ACCESS_TOKEN) private val accessToken: String,
    private val mediaControllerListenableFuture: ListenableFuture<MediaController>
) {
    fun getRecommendData() = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PAGE_SIZE / 2,
            initialLoadSize = PAGE_SIZE
        ),
        //请求网络数据
        remoteMediator = RecommendRemoteMediator(database, recommendService, deviceId, accessToken)
    ) {
        //从数据库拿到数据
        database.recommendDao().getRecommendData()
    }.liveData

    fun getDetailData(albumId: Long) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE * 2,
            prefetchDistance = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE * 2
        )
    ) {
        DetailPagingSource(albumId, recommendService, deviceId, accessToken)
    }.flow.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getPlayList() = liveData(Dispatchers.IO) {
        runCatching {
            musicWeService.getPlayList(
                mapOf(
                    "limit" to 10,
                    "total" to true,
                    "n" to 1000
                ).encryptWeAPI()
            ).also {
                emit(it)
            }
        }.onFailure {
            if (it is CancellationException) throw it
            it.printStackTrace()
        }
    }

    fun getDailyWord() = database.dailyWordDao()
        .getRecommendData()
        .filterNotNull()
        .onStart {
            refreshDailyWord()
        }
        .catch {
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)

    suspend fun refreshDailyWord() {
        runCatching {
            hitokotoService.getDailyWord()
        }.onSuccess {
            database.dailyWordDao().insertDailyWord(it)
        }.onFailure {
            if (it is CancellationException) throw it
            it.printStackTrace()
        }
    }

    fun getNewSong() = flow {
        musicWeService.getNewSong(
            mapOf(
                "type" to "recommend",
                "limit" to 10,
                "areaId" to 0
            ).encryptWeAPI()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getTopList() = flow {
        musicWeService.getTopList().also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getDailyList() = flow {
        musicWeService.getDailyList().also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getSongList(id: Long) = flow {
        musicWeService.getSongList(
            mapOf(
                "id" to "$id",
                "n" to "5000",
                "s" to "8"
            )
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun refreshLogin() = flow {
        musicWeService.refreshLogin(
            mapOf<String, String>().encryptWeAPI()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getAccountDetail() = flow {
        musicWeService.getAccountDetail().also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getUserPlaylists(id: Long) = flow {
        musicWeService.getUserPlaylist(
            mapOf(
                "uid" to "$id",
                "limit" to "1000",
                "includeVideo" to "false"
            )
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun loginCellphone(phone: String, password: String) = flow {
        musicWeService.loginCellphone(
            mapOf(
                "phone" to phone,
                "password" to password.toByteArray().md5().hex,
                "countrycode" to 86,
                "rememberLogin" to true
            ).encryptWeAPI()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun loginCaptcha(phone: String, captcha: String) = flow {
        musicWeService.loginCellphone(
            mapOf(
                "phone" to phone,
                "captcha" to captcha,
                "countrycode" to 86,
                "rememberLogin" to true
            ).encryptWeAPI()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun sendCaptcha(phone: String) = flow {
        musicWeService.sendCaptcha(
            mapOf("cellphone" to phone).encryptWeAPI()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getMusicUrl(id: String) = flow {
        urlService.getMusicUrl(
            mapOf(
                "ids" to "[$id]",
                "br" to 999000
            ).encryptEApi()
        ).also {
            emit(it)
        }
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)

    fun getMediaController() = flow {
        emit(mediaControllerListenableFuture.await())
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO)
}