package com.johannjara.walmart.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.johannjara.walmart.data.entity.ProductCacheEntity
import com.johannjara.walmart.data.entity.SearchHistoryEntity

@Database(
    entities = [SearchHistoryEntity::class, ProductCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun productCacheDao(): ProductCacheDao
}
