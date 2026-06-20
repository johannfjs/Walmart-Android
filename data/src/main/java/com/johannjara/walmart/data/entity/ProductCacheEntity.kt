package com.johannjara.walmart.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products_cache",
    indices = [Index(value = ["search_keyword"])]
)
data class ProductCacheEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "search_keyword")
    val searchKeyword: String,
    val title: String,
    val price: Double,
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String,
    @ColumnInfo(name = "cached_at_page")
    val cachedAtPage: Int
)
