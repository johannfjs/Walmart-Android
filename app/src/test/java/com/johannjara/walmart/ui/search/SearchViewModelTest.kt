package com.johannjara.walmart.ui.search

import com.johannjara.walmart.domain.model.SearchQuery
import com.johannjara.walmart.domain.repository.SearchHistoryRepository
import com.johannjara.walmart.domain.usecase.GetSearchHistoryUseCase
import com.johannjara.walmart.domain.usecase.SaveSearchQueryUseCase
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeSearchHistoryRepository
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var saveSearchQueryUseCase: SaveSearchQueryUseCase
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        repository = FakeSearchHistoryRepository()
        getSearchHistoryUseCase = GetSearchHistoryUseCase(repository)
        saveSearchQueryUseCase = SaveSearchQueryUseCase(repository)
        viewModel = SearchViewModel(getSearchHistoryUseCase, saveSearchQueryUseCase)
    }

    @Test
    fun searchHistoryState_initiallyLoading() = runTest {
        assertEquals(SearchHistoryState.Loading, viewModel.searchHistoryState.value)
    }

    @Test
    fun searchHistoryState_emitsEmpty_whenHistoryIsEmpty() = runTest {
        repository.emit(emptyList())
        assertEquals(SearchHistoryState.Empty, viewModel.searchHistoryState.first { it != SearchHistoryState.Loading })
    }

    @Test
    fun searchHistoryState_emitsSuccess_whenHistoryIsNotEmpty() = runTest {
        val history = listOf(SearchQuery("nintendo", 12345L))
        repository.emit(history)
        val state = viewModel.searchHistoryState.first { it != SearchHistoryState.Loading }
        assertEquals(SearchHistoryState.Success(history), state)
    }

    @Test
    fun onQueryChanged_updatesSearchQueryTextState() {
        viewModel.onQueryChanged("zelda")
        assertEquals("zelda", viewModel.searchQuery.value)
    }

    @Test
    fun onSearchTriggered_savesQueryAndClearsOrKeepsQuery() = runTest {
        viewModel.onQueryChanged("zelda")
        viewModel.onSearchTriggered()
        assertEquals(listOf("zelda"), repository.savedQueries)
    }

    @Test
    fun onHistoryItemClicked_updatesQueryAndSavesIt() = runTest {
        viewModel.onHistoryItemClicked("nintendo")
        assertEquals("nintendo", viewModel.searchQuery.value)
        assertEquals(listOf("nintendo"), repository.savedQueries)
    }
}

private class FakeSearchHistoryRepository : SearchHistoryRepository {
    private val flow = MutableSharedFlow<List<SearchQuery>>(replay = 1)
    val savedQueries = mutableListOf<String>()

    fun emit(list: List<SearchQuery>) {
        flow.tryEmit(list)
    }

    override fun getSearchHistory(): Flow<List<SearchQuery>> = flow

    override suspend fun saveSearchQuery(query: String) {
        savedQueries.add(query)
    }
}
