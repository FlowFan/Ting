package com.example.ting.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ting.model.Detail
import com.example.ting.other.Constants.APP_KEY
import com.example.ting.other.Constants.CLIENT_OS_TYPE
import com.example.ting.other.Constants.DEVICE_ID_TYPE
import com.example.ting.other.Constants.DEVICE_TYPE
import com.example.ting.other.Constants.PACK_ID
import com.example.ting.other.Constants.SDK_CLIENT_TYPE
import com.example.ting.other.Constants.SDK_VERSION
import com.example.ting.other.sig
import kotlinx.coroutines.CancellationException

class DetailPagingSource(
    private val albumId: Long,
    private val recommendService: RecommendService,
    private val deviceId: String,
    private val accessToken: String
) : PagingSource<Int, Detail.Track>() {
    override fun getRefreshKey(state: PagingState<Int, Detail.Track>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Detail.Track> =
        runCatching {
            val currentPage = params.key ?: 1
            val queryParams = mapOf(
                "album_id" to albumId,
                "app_key" to APP_KEY,
                "client_os_type" to CLIENT_OS_TYPE,
                "device_id" to deviceId,
                "device_id_type" to DEVICE_ID_TYPE,
                "device_type" to DEVICE_TYPE,
                "pack_id" to PACK_ID,
                "sdk_client_type" to SDK_CLIENT_TYPE,
                "sdk_version" to SDK_VERSION,
                "access_token" to accessToken,
                "page" to currentPage
            )
            recommendService.searchDetailData(queryParams + ("sig" to queryParams.sig())).let {
                val album = it.tracks
                val prevKey = if (currentPage > 1) currentPage - 1 else null
                val nextKey = if (album.isNotEmpty()) currentPage + 1 else null
                LoadResult.Page(album, prevKey, nextKey)
            }
        }.getOrElse {
            if (it is CancellationException) throw it
            it.printStackTrace()
            LoadResult.Error(it)
        }
}