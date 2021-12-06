package com.example.ximalaya.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.ximalaya.RecommendPagingSource
import javax.inject.Inject

class RecommendRepository @Inject constructor() {
    fun getRecommendData() = Pager(
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 10
        )
    ) {
        RecommendPagingSource()
    }.flow
}