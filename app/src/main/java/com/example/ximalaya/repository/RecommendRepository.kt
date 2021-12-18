package com.example.ximalaya.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.ximalaya.remote.RecommendPagingSource
import com.example.ximalaya.remote.RecommendService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecommendRepository @Inject constructor(
    private val recommendService: RecommendService
) {
    fun getRecommendData() = Pager(
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 10
        )
    ) {
        RecommendPagingSource(recommendService)
    }.flow.flowOn(Dispatchers.IO)
}