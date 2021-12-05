package com.example.ximalaya

import androidx.paging.Pager
import androidx.paging.PagingConfig

class RecommendRepository {
    fun getRecommendData() = Pager(
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 10
        )
    ) {
        RecommendPagingSource()
    }.flow
}