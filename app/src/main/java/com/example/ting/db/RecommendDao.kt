package com.example.ting.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ting.model.Album

@Dao
interface RecommendDao {
    @Insert(entity = Album::class)
    suspend fun insertRecommend(vararg album: Album)

    @Query("SELECT * FROM Album")
    fun getRecommendData(): PagingSource<Int, Album>

    @Query("DELETE FROM Album")
    suspend fun clearRecommendData()
}