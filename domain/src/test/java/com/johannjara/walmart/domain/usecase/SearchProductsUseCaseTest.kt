package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchProductsUseCaseTest {

    @Test
    fun invoke_returnsProductStreamFromRepository() = runTest {
        val expectedProducts = listOf(
            Product("1", "Sony TV", 499.99, "image1.jpg"),
            Product("2", "Sony Headphones", 199.99, "image2.jpg")
        )
        val repository = object : ProductRepository {
            override fun getCachedProductsStream(keyword: String): Flow<List<Product>> {
                assertEquals("sony", keyword)
                return flowOf(expectedProducts)
            }

            override suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit> {
                return Result.success(Unit)
            }
        }
        val useCase = SearchProductsUseCase(repository)

        val result = useCase("sony").first()
        assertEquals(expectedProducts, result)
    }
}
