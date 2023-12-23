package com.example.ting.remote

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ting.db.AppDatabase
import com.example.ting.init.AppInitializer
import com.example.ting.model.Album
import com.example.ting.other.Constants.APP_KEY
import com.example.ting.other.Constants.CLIENT_OS_TYPE
import com.example.ting.other.Constants.DEVICE_ID_TYPE
import com.example.ting.other.Constants.DEVICE_TYPE
import com.example.ting.other.Constants.LIKE_COUNT
import com.example.ting.other.Constants.PACK_ID
import com.example.ting.other.Constants.PAGE_SIZE
import com.example.ting.other.Constants.SDK_CLIENT_TYPE
import com.example.ting.other.Constants.SDK_VERSION
import com.example.ting.other.isConnectedNetwork
import com.example.ting.other.sig
import kotlinx.coroutines.CancellationException

class RecommendRemoteMediator(
    private val database: AppDatabase,
    private val recommendService: RecommendService,
    private val deviceId: String,
    private val accessToken: String
) : RemoteMediator<Int, Album>() {
    private val recommendSig by lazy {
        buildString {
            append("access_token=$accessToken")
            append("&app_key=$APP_KEY")
            append("&client_os_type=$CLIENT_OS_TYPE")
            append("&device_id=$deviceId")
            append("&device_id_type=$DEVICE_ID_TYPE")
            append("&device_type=$DEVICE_TYPE")
            append("&like_count=$LIKE_COUNT")
            append("&pack_id=$PACK_ID")
            append("&sdk_client_type=$SDK_CLIENT_TYPE")
            append("&sdk_version=$SDK_VERSION")
        }.sig()
    }
    private val recommendDao by lazy { database.recommendDao() }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Album>): MediatorResult =
        runCatching {
            if (!AppInitializer.mContext.isConnectedNetwork()) {
                return MediatorResult.Error(Throwable("网络连接失败"))
            }
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(false)
                    lastItem.page + 1
                }
            }
            recommendService.searchRecommendData(
                appKey = APP_KEY,
                clientOsType = CLIENT_OS_TYPE,
                deviceId = deviceId,
                deviceIdType = DEVICE_ID_TYPE,
                deviceType = DEVICE_TYPE,
                likeCount = LIKE_COUNT,
                packId = PACK_ID,
                sdkClientType = SDK_CLIENT_TYPE,
                sdkVersion = SDK_VERSION,
                accessToken = accessToken,
                sig = recommendSig
            ).map {
                it.copy(page = page)
            }.let {
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        recommendDao.clearRecommendData()
                    }
                    recommendDao.insertRecommend(*it.toTypedArray())
                }
                MediatorResult.Success(it.size < PAGE_SIZE)
            }
        }.getOrElse {
            if (it is CancellationException) throw it
            it.printStackTrace()
            MediatorResult.Error(it)
        }
}