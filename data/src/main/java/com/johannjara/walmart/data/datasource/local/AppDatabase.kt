package com.johannjara.walmart.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.johannjara.walmart.data.entity.DummyEntity

@Database(entities = [DummyEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
}
