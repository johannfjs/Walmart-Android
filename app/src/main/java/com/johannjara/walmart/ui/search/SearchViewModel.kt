package com.johannjara.walmart.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johannjara.walmart.domain.usecase.GetSearchHistoryUseCase
import com.johannjara.walmart.domain.usecase.SaveSearchQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchHistoryState: StateFlow<SearchHistoryState> = getSearchHistoryUseCase()
        .map { history ->
            if (history.isEmpty()) {
                SearchHistoryState.Empty
            } else {
                SearchHistoryState.Success(history)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchHistoryState.Loading
        )

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTriggered() {
        viewModelScope.launch {
            saveSearchQueryUseCase(_searchQuery.value)
        }
    }

    fun onHistoryItemClicked(query: String) {
        _searchQuery.value = query
        onSearchTriggered()
    }
}
