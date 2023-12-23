package com.example.ting.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ting.model.Album
import com.example.ting.model.DailyWord

@Database(
    entities = [Album::class, DailyWord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendDao(): AlbumDao
    abstract fun dailyWordDao(): DailyWordDao
}