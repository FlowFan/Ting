package com.example.ting.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ting.model.Playlists
import com.example.ting.other.encryptWeAPI

class TopPlaylistPagingSource(
    private val category: String,
    private val musicService: MusicWeService
) : PagingSource<Int, Playlists>() {
    override fun getRefreshKey(state: PagingState<Int, Playlists>) = 0

    override suspend fun load(params: LoadParams<Int>) = try {
        val currentPage = params.key ?: 0
        val topPlaylists = musicService.getTopPlaylist(
            mapOf(
                "cat" to category,
                "order" to "hot",
                "limit" to "${params.loadSize}",
                "offset" to "${currentPage * params.loadSize}",
                "total" to "true"
            ).encryptWeAPI()
        )
        val prevKey = if (currentPage > 0) currentPage - 1 else null
        val nextKey = if (topPlaylists.more) currentPage + 1 else null
        LoadResult.Page(topPlaylists.playlists, prevKey, nextKey)
    } catch (e: Exception) {
        e.stackTraceToString()
        LoadResult.Error(e)
    }
}