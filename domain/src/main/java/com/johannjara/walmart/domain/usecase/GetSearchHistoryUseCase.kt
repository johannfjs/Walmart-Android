package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<List<SearchQuery>> {
        return repository.getSearchHistory()
    }
}
