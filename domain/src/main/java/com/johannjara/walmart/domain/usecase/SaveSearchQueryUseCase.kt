package com.johannjara.walmart.domain.usecase

import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SaveSearchQueryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            repository.saveSearchQuery(trimmed)
        }
    }
}
