package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSearchHistoryUseCaseTest {

    @Test
    fun getSearchHistory_returnsRepositoryFlow() = runTest {
        val expectedList = listOf(
            SearchQuery("xbox", 1000L),
            SearchQuery("playstation", 900L)
        )
        val repository = object : SearchHistoryRepository {
            override fun getSearchHistory(): Flow<List<SearchQuery>> = flowOf(expectedList)
            override suspend fun saveSearchQuery(query: String) = Unit
        }
        val useCase = GetSearchHistoryUseCase(repository)

        val result = useCase().first()
        assertEquals(expectedList, result)
    }
}
