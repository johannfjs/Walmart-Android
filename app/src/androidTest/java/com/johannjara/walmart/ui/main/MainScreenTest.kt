package com.johannjara.walmart.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.johannjara.walmart.domain.model.Product
import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.ProductRepository
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import com.johannjara.walmart.domain.usecase.FetchNextPageUseCase
import com.johannjara.walmart.domain.usecase.GetSearchHistoryUseCase
import com.johannjara.walmart.domain.usecase.SaveSearchQueryUseCase
import com.johannjara.walmart.domain.usecase.SearchProductsUseCase
import com.johannjara.walmart.ui.search.ProductSearchViewModel
import com.johannjara.walmart.ui.search.SearchViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private val productRepository = object : ProductRepository {
    override fun getCachedProductsStream(keyword: String) = flowOf(emptyList<Product>())
    override suspend fun fetchAndCacheNextPage(keyword: String, page: Int) = Result.success(Unit)
  }

  private val searchHistoryRepository = object : SearchHistoryRepository {
    override fun getSearchHistory() = flowOf(emptyList<SearchQuery>())
    override suspend fun saveSearchQuery(query: String) {}
  }

  private lateinit var searchViewModel: SearchViewModel
  private lateinit var productSearchViewModel: ProductSearchViewModel

  @Before
  fun setup() {
    searchViewModel = SearchViewModel(
      getSearchHistoryUseCase = GetSearchHistoryUseCase(searchHistoryRepository),
      saveSearchQueryUseCase = SaveSearchQueryUseCase(searchHistoryRepository)
    )
    productSearchViewModel = ProductSearchViewModel(
      searchProductsUseCase = SearchProductsUseCase(productRepository),
      fetchNextPageUseCase = FetchNextPageUseCase(productRepository)
    )

    composeTestRule.setContent {
      MainScreen(
        onItemClick = {},
        searchViewModel = searchViewModel,
        productSearchViewModel = productSearchViewModel
      )
    }
  }

  @Test
  fun searchField_exists() {
    composeTestRule.onNodeWithText("Search").assertExists()
  }
}
