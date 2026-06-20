package com.johannjara.walmart.ui.search

import com.johannjara.walmart.domain.model.SearchQuery

sealed interface SearchHistoryState {
    object Loading : SearchHistoryState
    object Empty : SearchHistoryState
    data class Success(val history: List<SearchQuery>) : SearchHistoryState
}
