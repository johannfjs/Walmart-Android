package com.johannjara.walmart.domain.repository

import com.johannjara.walmart.domain.model.SearchQuery
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<SearchQuery>>
    suspend fun saveSearchQuery(query: String)
}
