package com.johannjara.walmart.ui.search

import com.johannjara.walmart.domain.model.Product

sealed interface ProductSearchUiState {
    object Idle : ProductSearchUiState
    object Loading : ProductSearchUiState
    data class Success(
        val products: List<Product>,
        val isFetchingNextPage: Boolean = false
    ) : ProductSearchUiState
    data class Error(val message: String) : ProductSearchUiState
    data class Empty(val keyword: String) : ProductSearchUiState
}
