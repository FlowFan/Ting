package com.example.ximalaya.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ximalaya.model.Album

@Database(entities = [Album::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendDao(): RecommendDao
}