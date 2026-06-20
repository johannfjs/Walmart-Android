package com.johannjara.walmart.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.johannjara.walmart.ui.search.EmptyScreen
import com.johannjara.walmart.ui.search.ErrorScreen
import com.johannjara.walmart.ui.search.ProductLazyList
import com.johannjara.walmart.ui.search.ProductSearchUiState
import com.johannjara.walmart.ui.search.ProductSearchViewModel
import com.johannjara.walmart.ui.search.SearchHistoryList
import com.johannjara.walmart.ui.search.SearchViewModel

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  searchViewModel: SearchViewModel = hiltViewModel(),
  productSearchViewModel: ProductSearchViewModel = hiltViewModel(),
) {
  val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
  val searchHistoryState by searchViewModel.searchHistoryState.collectAsStateWithLifecycle()
  val productUiState by productSearchViewModel.uiState.collectAsStateWithLifecycle()

  val focusManager = LocalFocusManager.current
  var isFocused by remember { mutableStateOf(false) }

  Column(modifier = modifier) {
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchViewModel.onQueryChanged(it) },
      modifier = Modifier
        .fillMaxWidth()
        .onFocusChanged { isFocused = it.isFocused },
      label = { Text("Search") },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search Icon"
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchViewModel.onQueryChanged("") }) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear Search"
            )
          }
        }
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(onSearch = {
        searchViewModel.onSearchTriggered()
        productSearchViewModel.searchProducts(searchQuery)
        focusManager.clearFocus()
      }),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    val showHistory = isFocused || searchQuery.isEmpty()

    if (showHistory) {
      SearchHistoryList(
        state = searchHistoryState,
        onItemClick = { query ->
          searchViewModel.onHistoryItemClicked(query)
          productSearchViewModel.searchProducts(query)
          focusManager.clearFocus()
        }
      )
    } else {
      when (val productState = productUiState) {
        ProductSearchUiState.Idle -> {
          // Show nothing or prompt
        }
        ProductSearchUiState.Loading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        }
        is ProductSearchUiState.Success -> {
          ProductLazyList(
            products = productState.products,
            isFetchingNextPage = productState.isFetchingNextPage,
            onLoadMore = { productSearchViewModel.loadNextPage() },
            modifier = Modifier.fillMaxSize()
          )
        }
        is ProductSearchUiState.Error -> {
          ErrorScreen(
            message = productState.message,
            onRetry = { productSearchViewModel.retry() },
            modifier = Modifier.fillMaxSize()
          )
        }
        is ProductSearchUiState.Empty -> {
          EmptyScreen(
            keyword = productState.keyword,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}
