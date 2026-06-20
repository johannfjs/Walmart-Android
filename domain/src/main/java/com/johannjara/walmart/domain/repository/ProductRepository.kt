package com.johannjara.walmart.domain.repository

import com.johannjara.walmart.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getCachedProductsStream(keyword: String): Flow<List<Product>>
    suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit>
}
