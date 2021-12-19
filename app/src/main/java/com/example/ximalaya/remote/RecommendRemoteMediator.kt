package com.example.ximalaya.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ximalaya.db.AppDatabase
import com.example.ximalaya.init.AppInitializer
import com.example.ximalaya.model.Album
import com.example.ximalaya.other.isConnectedNetwork
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class RecommendRemoteMediator(
    private val recommendService: RecommendService,
    private val database: AppDatabase
) : RemoteMediator<Int, Album>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Album>): MediatorResult {
        return try {
            val album = recommendService.searchRecommendData()
            val recommendDao = database.recommendDao()
            if (!AppInitializer.mContext.isConnectedNetwork()) {
                return MediatorResult.Success(true)
            }
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    recommendDao.clearRecommendData()
                }
                recommendDao.insertRecommend(album)
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
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }
}

/*
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
}*/
