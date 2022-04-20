package com.example.ting.remote

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ting.db.AppDatabase
import com.example.ting.init.AppInitializer
import com.example.ting.model.Album
import com.example.ting.other.Constants.LIKE_KEY
import com.example.ting.other.isConnectedNetwork
import com.example.ting.other.sig
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager

@OptIn(ExperimentalPagingApi::class)
class RecommendRemoteMediator(
    private val recommendService: RecommendService,
    private val database: AppDatabase
) : RemoteMediator<Int, Album>() {
    private val accessToken by lazy { AccessTokenManager.getInstanse().accessToken }
    private val recommendSig by lazy { "access_token=$accessToken&$LIKE_KEY".sig() }
    private val recommendDao by lazy { database.recommendDao() }

    @SuppressLint("HardwareIds")
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Album>): MediatorResult {
        return try {
            println(
                Settings.Secure.getString(
                    AppInitializer.mContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
            val album = recommendService.searchRecommendData(accessToken, recommendSig)
            if (!AppInitializer.mContext.isConnectedNetwork()) {
                return MediatorResult.Success(true)
            }
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    recommendDao.clearRecommendData()
                }
                recommendDao.insertRecommend(*album.toTypedArray())
            }
            when (loadType) {
                LoadType.REFRESH -> return MediatorResult.Success(false)
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    state.lastItemOrNull() ?: return MediatorResult.Success(true)
                    return MediatorResult.Success(false)
                }
            }
        } catch (e: Exception) {
            e.stackTraceToString()
            MediatorResult.Error(e)
        }
    }
}
