package com.johannjara.walmart.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johannjara.walmart.domain.usecase.FetchNextPageUseCase
import com.johannjara.walmart.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val fetchNextPageUseCase: FetchNextPageUseCase
) : ViewModel() {

    private val activeKeyword = MutableStateFlow("")
    private var currentPage = 1

    private val isLoadingFirstPage = MutableStateFlow(false)
    private val isFetchingNextPage = MutableStateFlow(false)
    private val networkError = MutableStateFlow<String?>(null)

    private val productsFlow = activeKeyword.flatMapLatest { keyword ->
        if (keyword.isEmpty()) {
            flowOf(emptyList())
        } else {
            searchProductsUseCase(keyword)
        }
    }

    val uiState: StateFlow<ProductSearchUiState> = combine(
        activeKeyword,
        productsFlow,
        isFetchingNextPage,
        networkError,
        isLoadingFirstPage
    ) { keyword, products, nextPageLoading, error, firstPageLoading ->
        when {
            keyword.isEmpty() -> ProductSearchUiState.Idle
            firstPageLoading -> ProductSearchUiState.Loading
            error != null && products.isEmpty() -> ProductSearchUiState.Error(error)
            products.isEmpty() -> ProductSearchUiState.Empty(keyword)
            else -> ProductSearchUiState.Success(products, nextPageLoading)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductSearchUiState.Idle
    )

    fun searchProducts(query: String) {
        val trimmed = query.trim()
        activeKeyword.value = trimmed
        if (trimmed.isEmpty()) {
            return
        }

        currentPage = 1
        isLoadingFirstPage.value = true
        networkError.value = null

        viewModelScope.launch {
            val result = fetchNextPageUseCase(trimmed, 1)
            isLoadingFirstPage.value = false
            if (result.isFailure) {
                val exception = result.exceptionOrNull()
                networkError.value = if (exception is java.io.IOException) {
                    "no cuentas con Internet"
                } else {
                    exception?.message ?: "Unknown error"
                }
            }
        }
    }

    fun retry() {
        val keyword = activeKeyword.value
        if (keyword.isNotEmpty()) {
            searchProducts(keyword)
        }
    }

    fun loadNextPage() {
        val keyword = activeKeyword.value
        if (keyword.isEmpty() || isLoadingFirstPage.value || isFetchingNextPage.value) {
            return
        }

        val currentState = uiState.value
        if (currentState !is ProductSearchUiState.Success) {
            return
        }

        isFetchingNextPage.value = true
        networkError.value = null

        viewModelScope.launch {
            val nextPage = currentPage + 1
            val result = fetchNextPageUseCase(keyword, nextPage)
            isFetchingNextPage.value = false
            if (result.isSuccess) {
                currentPage = nextPage
            } else {
                val exception = result.exceptionOrNull()
                networkError.value = if (exception is java.io.IOException) {
                    "no cuentas con Internet"
                } else {
                    exception?.message ?: "Unknown error"
                }
            }
        }
    }
}
