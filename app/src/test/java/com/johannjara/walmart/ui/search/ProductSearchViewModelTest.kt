package com.johannjara.walmart.ui.search

import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.repository.ProductRepository
import com.johannjara.walmart.domain.usecase.FetchNextPageUseCase
import com.johannjara.walmart.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ProductSearchMainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class ProductSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = ProductSearchMainDispatcherRule()

    private lateinit var repository: FakeProductRepository
    private lateinit var searchProductsUseCase: SearchProductsUseCase
    private lateinit var fetchNextPageUseCase: FetchNextPageUseCase
    private lateinit var viewModel: ProductSearchViewModel

    @Before
    fun setUp() {
        repository = FakeProductRepository()
        searchProductsUseCase = SearchProductsUseCase(repository)
        fetchNextPageUseCase = FetchNextPageUseCase(repository)
        viewModel = ProductSearchViewModel(searchProductsUseCase, fetchNextPageUseCase)
    }

    @Test
    fun initialUiState_isIdle() {
        assertEquals(ProductSearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun searchProducts_withEmptyQuery_transitionsToIdle() = runTest {
        viewModel.searchProducts("")
        assertEquals(ProductSearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun searchProducts_withValidQuery_fetchesPage1AndEmitsSuccess() = runTest {
        val products = listOf(Product("1", "Sony TV", 499.99, "img.jpg"))
        repository.emit(products)

        viewModel.searchProducts("sony")

        val successState = viewModel.uiState.first { it is ProductSearchUiState.Success } as ProductSearchUiState.Success
        assertEquals(products, successState.products)
        assertEquals(1, repository.fetchedPages.size)
        assertEquals(1, repository.fetchedPages[0])
    }

    @Test
    fun loadNextPage_triggersIncrementalPageFetch() = runTest {
        val products = listOf(Product("1", "Sony TV", 499.99, "img.jpg"))
        repository.emit(products)

        viewModel.searchProducts("sony")
        viewModel.uiState.first { it is ProductSearchUiState.Success }

        viewModel.loadNextPage()

        assertEquals(2, repository.fetchedPages.size)
        assertEquals(1, repository.fetchedPages[0])
        assertEquals(2, repository.fetchedPages[1])
    }

    @Test
    fun loadNextPage_whenAlreadyLoading_doesNotTriggerDuplicateRequest() = runTest {
        val products = listOf(Product("1", "Sony TV", 499.99, "img.jpg"))
        repository.emit(products)

        viewModel.searchProducts("sony")
        viewModel.uiState.first { it is ProductSearchUiState.Success }

        repository.delayMs = 1000L

        // Start page 2 fetch
        viewModel.loadNextPage()
        // Try page 2 fetch again
        viewModel.loadNextPage()

        assertEquals(2, repository.fetchedPages.size)
    }
}

private class FakeProductRepository : ProductRepository {
    private val flow = MutableSharedFlow<List<Product>>(replay = 1)
    val fetchedPages = mutableListOf<Int>()
    var delayMs: Long = 0

    fun emit(list: List<Product>) {
        flow.tryEmit(list)
    }

    override fun getCachedProductsStream(keyword: String): Flow<List<Product>> = flow

    override suspend fun fetchAndCacheNextPage(keyword: String, page: Int): Result<Unit> {
        fetchedPages.add(page)
        if (delayMs > 0) {
            kotlinx.coroutines.delay(delayMs)
        }
        return Result.success(Unit)
    }
}
