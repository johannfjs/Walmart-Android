package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.repository.ProductRepository
import javax.inject.Inject

class FetchNextPageUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(keyword: String, page: Int): Result<Unit> {
        return repository.fetchAndCacheNextPage(keyword, page)
    }
}
