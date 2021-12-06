package com.example.ximalaya

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ximalaya.ting.android.opensdk.model.album.Album
import java.lang.Exception

class RecommendPagingSource : PagingSource<Int, Album>() {
    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> = try {
        val currentPage = params.key ?: 1
        val album = RecommendUtil.getRecommendData()
        val prevKey = if (currentPage > 1) currentPage - 1 else null
        val nextKey = currentPage + 1
        LoadResult.Page(album, prevKey, nextKey)
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }
}