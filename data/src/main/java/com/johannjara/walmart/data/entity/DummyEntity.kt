package com.johannjara.walmart.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dummy")
data class DummyEntity(
    @PrimaryKey val id: Int,
    val value: String
)
