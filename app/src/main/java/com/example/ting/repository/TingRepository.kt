package com.example.ting.repository

import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.ting.db.AppDatabase
import com.example.ting.other.encryptWeAPI
import com.example.ting.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TingRepository @Inject constructor(
    private val recommendService: RecommendService,
    private val musicWeService: MusicWeService,
    private val hitokotoService: HitokotoService,
    private val database: AppDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getRecommendData() = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10),
        //请求网络数据
        remoteMediator = RecommendRemoteMediator(recommendService, database)
    ) {
        //从数据库拿到数据
        database.recommendDao().getRecommendData()
    }.flow.flowOn(Dispatchers.IO)

    fun getDetailData(albumId: Int) = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20)
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
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }

    fun getDailyWord() = flow {
        try {
            emit(hitokotoService.getDailyWord())
        } catch (e: Exception) {
            e.stackTraceToString()
        }
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

    fun getSongList(id: Long) = liveData(Dispatchers.IO) {
        try {
            val songList = musicWeService.getSongList(
                mapOf(
                    "id" to "$id",
                    "n" to "5000",
                    "s" to "8"
                )
            )
            println(songList.playlist.tracks.size)
            emit(songList)
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }
}