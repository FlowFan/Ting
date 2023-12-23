package com.example.ting.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ting.model.AccountDetail
import com.example.ting.model.Album
import com.example.ting.model.DailyWord
import com.example.ting.model.PlayList

@Database(
    entities = [Album::class, AccountDetail::class, DailyWord::class, PlayList.Result::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendDao(): AlbumDao
    abstract fun dailyWordDao(): DailyWordDao
}