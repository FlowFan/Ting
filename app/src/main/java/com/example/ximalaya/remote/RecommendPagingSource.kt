package com.example.ximalaya.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ximalaya.model.Album
import java.lang.Exception

class RecommendPagingSource(private val recommendService: RecommendService) :
    PagingSource<Int, Album>() {
    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> =
        try {
            val currentPage = params.key ?: 1
            val album = recommendService.searchRecommendData()
            val prevKey = if (currentPage > 1) currentPage - 1 else null
            val nextKey = if (album.isNotEmpty()) currentPage + 1 else null
            LoadResult.Page(album, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
}