package com.johannjara.walmart.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.johannjara.walmart.data.entity.DummyEntity
import com.johannjara.walmart.data.entity.SearchHistoryEntity

@Database(
    entities = [DummyEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
