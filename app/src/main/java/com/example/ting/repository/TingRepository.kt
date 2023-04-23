package com.example.ting.repository

import android.content.Context
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.ting.db.AppDatabase
import com.example.ting.init.AppInitializer
import com.example.ting.other.encryptEApi
import com.example.ting.other.encryptWeAPI
import com.example.ting.remote.*
import korlibs.crypto.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TingRepository @Inject constructor(
    private val recommendService: RecommendService,
    private val musicWeService: MusicWeService,
    private val urlService: UrlService,
    private val hitokotoService: HitokotoService,
    private val database: AppDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getRecommendData() = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10
        ),
        //请求网络数据
        remoteMediator = RecommendRemoteMediator(recommendService, database)
    ) {
        //从数据库拿到数据
        database.recommendDao().getRecommendData()
    }.flow.flowOn(Dispatchers.IO)

    fun getDetailData(albumId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20
        )
    ) {
        DetailPagingSource(albumId)
    }.flow.flowOn(Dispatchers.IO)

    fun getPlayList() = liveData(Dispatchers.IO) {
        try {
            val playList = musicWeService.getPlayList(
                mapOf(
                    "limit" to "10",
                    "total" to "true",
                    "n" to "1000"
                ).encryptWeAPI()
            )
            emit(playList)
            database.playListDao().insertPlayList(playList.result)
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getDailyWord() = flow {
        val result = hitokotoService.getDailyWord()
        emit(result)
        database.dailyWordDao().insertDailyWord(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getNewSong() = liveData(Dispatchers.IO) {
        try {
            val newSong = musicWeService.getNewSong(
                mapOf(
                    "type" to "recommend",
                    "limit" to "10",
                    "areaId" to "0"
                ).encryptWeAPI()
            )
            emit(newSong)
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getTopList() = liveData(Dispatchers.IO) {
        try {
            emit(musicWeService.getTopList())
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getDailyList() = liveData(Dispatchers.IO) {
        try {
            emit(musicWeService.getDailyList())
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getSongList(id: Long) = flow {
        val songList = musicWeService.getSongList(
            mapOf(
                "id" to "$id",
                "n" to "5000",
                "s" to "8"
            )
        )
        emit(songList)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getTypeList() = liveData(Dispatchers.IO) {
        try {
            val result = musicWeService.getTypeList(mapOf<String, String>().encryptWeAPI())
            emit(result)
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getHotPlaylistTags() = flow {
        val sharedPreferences = AppInitializer.mContext.getSharedPreferences("playlist_category", Context.MODE_PRIVATE)
        val result = if (sharedPreferences.contains("data")) {
            // 载入用户自定义歌单category
            sharedPreferences.getString("data", "")?.split(",") ?: emptyList()
        } else {
            // 载入热门category
            musicWeService.getHotPlaylistTags(
                mapOf<String, String>().encryptWeAPI()
            ).tags.map { it.name }
        }
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getHighQualityPlaylist() = liveData(Dispatchers.IO) {
        try {
            val result = musicWeService.getHighQualityPlaylist(
                mapOf(
                    "cat" to "全部",
                    "limit" to "100",
                    "lasttime" to "0",
                    "total" to "true"
                )
            )
            emit(result)
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getTopPlaylist(category: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 3,
            initialLoadSize = 20
        )
    ) {
        TopPlaylistPagingSource(category, musicWeService)
    }.flow.flowOn(Dispatchers.IO)

    fun loginCellphone(phone: String, password: String) = flow {
        delay(500)
        val result = musicWeService.loginCellphone(
            mapOf(
                "phone" to phone,
                "countrycode" to "86",
                "password" to password.toByteArray().md5().hex,
                "rememberLogin" to "true"
            ).encryptWeAPI()
        )
        emit(result)
        database.loginResponseDao().insertLoginResponse(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun loginCaptcha(phone: String, captcha: String) = flow {
        delay(500)
        val result = musicWeService.loginCellphone(
            mapOf(
                "phone" to phone,
                "captcha" to captcha,
                "countrycode" to "86",
                "rememberLogin" to "true"
            ).encryptWeAPI()
        )
        emit(result)
        database.loginResponseDao().insertLoginResponse(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun sendCaptcha(phone: String) = flow {
        val result = musicWeService.sendCaptcha(mapOf("cellphone" to phone).encryptWeAPI())
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun refreshLogin() = flow {
        val result = musicWeService.refreshLogin(mapOf<String, String>().encryptWeAPI())
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getAccountDetail() = flow {
        val result = musicWeService.getAccountDetail()
        emit(result)
        database.accountDetailDao().insertAccountDetail(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getUserPlaylists(id: Long) = flow {
        val result = musicWeService.getUserPlaylist(
            mapOf(
                "uid" to "$id",
                "limit" to "1000",
                "includeVideo" to "false"
            )
        )
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun subPlaylist(id: Long, sub: Boolean) = flow {
        val result = musicWeService.subPlaylist(
            if (sub) "unsubscribe" else "subscribe",
            mapOf("id" to "$id").encryptWeAPI()
        )
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun likeMusic(id: Long, like: Boolean) = flow {
        val result = musicWeService.like(
            like,
            mapOf(
                "alg" to "itembased",
                "trackId" to "$id",
                "like" to "$like",
                "time" to "3"
            )
        )
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getLikeList(id: Long) = flow {
        val result = musicWeService.getLikeList(mapOf("uid" to "$id").encryptWeAPI())
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getMusicDetail(id: Long) = flow {
        val result = musicWeService.getMusicDetail(mapOf("c" to "[{\"id\":$id}]"))
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getLyric(id: Long) = flow {
        val result = musicWeService.getLyric(
            mapOf(
                "id" to "$id",
                "lv" to "-1",
                "kv" to "-1",
                "tv" to "-1"
            )
        )
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)

    fun getMusicUrl(id: Long) = flow {
        val result = urlService.getMusicUrl(
            mapOf(
                "ids" to "[$id]",
                "br" to "999000"
            ).encryptEApi()
        )
        emit(result)
    }.catch {
        it.stackTraceToString()
    }.flowOn(Dispatchers.IO)
}