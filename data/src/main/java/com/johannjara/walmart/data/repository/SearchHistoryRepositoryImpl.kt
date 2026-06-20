package com.johannjara.walmart.data.repository

import com.johannjara.walmart.data.datasource.local.SearchHistoryDao
import com.johannjara.walmart.data.entity.SearchHistoryEntity
import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getSearchHistory(): Flow<List<SearchQuery>> {
        return searchHistoryDao.getSearchHistory().map { list ->
            list.map { entity ->
                SearchQuery(
                    text = entity.queryText,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        val entry = SearchHistoryEntity(
            queryText = query,
            timestamp = System.currentTimeMillis()
        )
        searchHistoryDao.insert(entry)
    }
}
