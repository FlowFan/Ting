package com.example.ting.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ximalaya.ting.android.opensdk.model.track.Track

class DetailPagingSource(private val albumId: Int) : PagingSource<Int, Track>() {
    override fun getRefreshKey(state: PagingState<Int, Track>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> =
        try {
            val currentPage = params.key ?: 1
            val album = XimalayaService.getAlbumDetail(albumId, currentPage)
            val prevKey = if (currentPage > 1) currentPage - 1 else null
            val nextKey = if (album.isNotEmpty()) currentPage + 1 else null
            LoadResult.Page(album, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
}