package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveSearchQueryUseCaseTest {

    private val repository = FakeSearchHistoryRepository()
    private val useCase = SaveSearchQueryUseCase(repository)

    @Test
    fun saveSearchQuery_withEmptyString_doesNotCallRepository() = runTest {
        useCase("")
        assertTrue(repository.savedQueries.isEmpty())
    }

    @Test
    fun saveSearchQuery_withBlankString_doesNotCallRepository() = runTest {
        useCase("    ")
        assertTrue(repository.savedQueries.isEmpty())
    }

    @Test
    fun saveSearchQuery_withValidString_trimsAndCallsRepository() = runTest {
        useCase("  Nintendo Switch  ")
        assertEquals(1, repository.savedQueries.size)
        assertEquals("Nintendo Switch", repository.savedQueries[0])
    }
}

private class FakeSearchHistoryRepository : SearchHistoryRepository {
    val savedQueries = mutableListOf<String>()
    override fun getSearchHistory(): Flow<List<SearchQuery>> = throw NotImplementedError()
    override suspend fun saveSearchQuery(query: String) {
        savedQueries.add(query)
    }
}
