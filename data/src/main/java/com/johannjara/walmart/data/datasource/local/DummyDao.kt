package com.johannjara.walmart.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.johannjara.walmart.data.entity.DummyEntity

@Dao
interface DummyDao {
    @Query("SELECT * FROM dummy")
    suspend fun getAll(): List<DummyEntity>

    @Insert
    suspend fun insert(entity: DummyEntity)
}
