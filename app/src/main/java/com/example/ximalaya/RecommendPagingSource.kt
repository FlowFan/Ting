package com.example.ximalaya

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecommendPagingSource : PagingSource<Int, Album>() {
    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> = try {
        val currentPage = params.key ?: 1
        val album = getRecommendData()
        val prevKey = if (currentPage > 1) currentPage - 1 else null
        val nextKey = currentPage + 1
        LoadResult.Page(album, prevKey, nextKey)
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    //获取推荐内容
    private suspend fun getRecommendData() = suspendCoroutine<List<Album>> {
        val map = HashMap<String, String>()
        map[DTransferConstants.LIKE_COUNT] = Constants.RECOMMEND_COUNT.toString()
        CommonRequest.getGuessLikeAlbum(map, object : IDataCallBack<GussLikeAlbumList> {
            override fun onSuccess(p0: GussLikeAlbumList?) {
                it.resume(p0?.albumList!!)
            }

            override fun onError(p0: Int, p1: String) {
                Log.d("Hello", "onError: $p0")
                Log.d("Hello", "onError: $p1")
            }
        })
    }
}