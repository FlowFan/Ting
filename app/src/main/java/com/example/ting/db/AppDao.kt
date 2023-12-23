package com.example.ting.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.ting.model.DailyWord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWordDao {
    @Upsert(entity = DailyWord::class)
    suspend fun insertDailyWord(vararg dailyWord: DailyWord)

    @Query("SELECT * FROM DailyWord ORDER BY id DESC LIMIT 1")
    fun getRecommendData(): Flow<DailyWord>
}