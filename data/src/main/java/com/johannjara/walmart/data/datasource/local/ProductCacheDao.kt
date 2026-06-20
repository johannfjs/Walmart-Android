package com.johannjara.walmart.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.johannjara.walmart.data.entity.ProductCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCacheDao {

    @Query("SELECT * FROM products_cache WHERE search_keyword = :keyword ORDER BY cached_at_page ASC, id ASC")
    fun getCachedProductsStream(keyword: String): Flow<List<ProductCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductCacheEntity>)

    @Query("DELETE FROM products_cache WHERE search_keyword = :keyword")
    suspend fun deleteProductsByKeyword(keyword: String)
}
