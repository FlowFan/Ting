package com.example.ximalaya.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.ximalaya.db.AppDatabase
import com.example.ximalaya.remote.RecommendRemoteMediator
import com.example.ximalaya.remote.RecommendService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecommendRepository @Inject constructor(
    private val recommendService: RecommendService,
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
}