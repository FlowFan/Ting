package com.example.ting.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ting.model.*

@Database(
    entities = [Album::class, AccountDetail::class, DailyWord::class, LoginResponse::class, PlayList.Result::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendDao(): AlbumDao
    abstract fun accountDetailDao(): AccountDetailDao
    abstract fun dailyWordDao(): DailyWordDao
    abstract fun loginResponseDao(): LoginResponseDao
    abstract fun playListDao(): PlayListDao
}