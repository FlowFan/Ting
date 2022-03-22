package com.example.ting.remote

import android.util.Log
import com.example.ting.other.Constants.LIKE_COUNT
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.model.track.TrackList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object XimalayaService {
    //获取推荐内容
    suspend fun getRecommendData() = suspendCoroutine<List<Album>> {
        val map = HashMap<String, String>()
        map[DTransferConstants.LIKE_COUNT] = LIKE_COUNT.toString()
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

    suspend fun getAlbumDetail(albumId: Int, page: Int) = suspendCoroutine<List<Track>> {
        val map = HashMap<String, String>()
        map[DTransferConstants.ALBUM_ID] = albumId.toString()
        map[DTransferConstants.PAGE] = page.toString()
        CommonRequest.getTracks(map, object : IDataCallBack<TrackList> {
            override fun onSuccess(p0: TrackList?) {
                it.resume(p0?.tracks!!)
            }

            override fun onError(p0: Int, p1: String?) {
                Log.d("Hello", "onError: $p0")
                Log.d("Hello", "onError: $p1")
            }
        })
    }
}