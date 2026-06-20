package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(keyword: String): Flow<List<Product>> {
        return repository.getCachedProductsStream(keyword)
    }
}
