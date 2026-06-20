package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchNextPageUseCaseTest {

    @Test
    fun invoke_success_returnsSuccessResult() = runTest {
        val repository = object : ProductRepository {
            override fun getCachedProductsStream(keyword: String): Flow<List<Product>> = emptyFlow()

            override suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit> {
                assertEquals("sony", keyword)
                assertEquals(2, page)
                return Result.success(Unit)
            }
        }
        val useCase = FetchNextPageUseCase(repository)

        val result = useCase("sony", 2)
        assertTrue(result.isSuccess)
    }

    @Test
    fun invoke_failure_returnsFailureResult() = runTest {
        val exception = RuntimeException("Network Error")
        val repository = object : ProductRepository {
            override fun getCachedProductsStream(keyword: String): Flow<List<Product>> = emptyFlow()

            override suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit> {
                return Result.failure(exception)
            }
        }
        val useCase = FetchNextPageUseCase(repository)

        val result = useCase("sony", 2)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
