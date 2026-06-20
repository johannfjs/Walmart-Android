package com.johannjara.walmart.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.johannjara.walmart.theme.WalmartTheme
import com.johannjara.walmart.ui.search.SearchHistoryList
import com.johannjara.walmart.ui.search.SearchViewModel

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  mainViewModel: MainScreenViewModel = hiltViewModel(),
  searchViewModel: SearchViewModel = hiltViewModel(),
) {
  val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
  val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
  val searchHistoryState by searchViewModel.searchHistoryState.collectAsStateWithLifecycle()

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
          focusManager.clearFocus()
        }
      )
    } else {
      when (uiState) {
        MainScreenUiState.Loading -> {
          // Blank
        }
        is MainScreenUiState.Success -> {
          MainScreen(data = (uiState as MainScreenUiState.Success).data)
        }
        is MainScreenUiState.Error -> {
          Text("Error loading data: ${(uiState as MainScreenUiState.Error).throwable.message}")
        }
      }
    }
  }
}

@Composable
internal fun MainScreen(data: List<String>, modifier: Modifier = Modifier) {
  Column(modifier) { data.forEach { Greeting(it) } }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  WalmartTheme { MainScreen(listOf("Android")) }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
fun MainScreenPortraitPreview() {
  WalmartTheme { MainScreen(listOf("Android")) }
}
