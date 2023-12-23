package com.example.ting.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ting.model.Album

@Dao
interface AlbumDao {
    @Insert(entity = Album::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommend(vararg album: Album)

    @Query("SELECT * FROM Album")
    fun getRecommendData(): PagingSource<Int, Album>

    @Query("DELETE FROM Album")
    suspend fun clearRecommendData()
}