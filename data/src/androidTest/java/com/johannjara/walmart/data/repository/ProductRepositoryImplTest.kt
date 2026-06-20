package com.johannjara.walmart.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johannjara.walmart.data.datasource.local.AppDatabase
import com.johannjara.walmart.data.datasource.local.ProductCacheDao
import com.johannjara.walmart.data.datasource.remote.WalmartApiService
import com.johannjara.walmart.data.datasource.remote.WalmartImageInfo
import com.johannjara.walmart.data.datasource.remote.WalmartInitialData
import com.johannjara.walmart.data.datasource.remote.WalmartItemStack
import com.johannjara.walmart.data.datasource.remote.WalmartProductDto
import com.johannjara.walmart.data.datasource.remote.WalmartSearchItemContainer
import com.johannjara.walmart.data.datasource.remote.WalmartSearchPageProps
import com.johannjara.walmart.data.datasource.remote.WalmartSearchResult
import com.johannjara.walmart.data.datasource.remote.WalmartSearchProps
import com.johannjara.walmart.data.datasource.remote.WalmartSearchResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProductRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductCacheDao
    private lateinit var repository: ProductRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.productCacheDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun fetchAndCacheNextPage_success_savesToLocalCache() = runTest(testDispatcher) {
        val fakeResponse = WalmartSearchResponse(
            item = WalmartSearchItemContainer(
                props = WalmartSearchProps(
                    pageProps = WalmartSearchPageProps(
                        initialData = WalmartInitialData(
                            searchResult = WalmartSearchResult(
                                itemStacks = listOf(
                                    WalmartItemStack(
                                        items = listOf(
                                            WalmartProductDto(
                                                usItemId = "101",
                                                name = "Sony TV",
                                                imageInfo = WalmartImageInfo("http://img1.jpg"),
                                                price = 499.99
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val fakeApi = object : WalmartApiService {
            override suspend fun searchProducts(keyword: String, page: Int, sortBy: String): WalmartSearchResponse {
                return fakeResponse
            }
        }

        repository = ProductRepositoryImpl(fakeApi, dao, testDispatcher)

        val result = repository.fetchAndCacheNextPage("sony", 1)
        assertTrue(result.isSuccess)

        val cached = repository.getCachedProductsStream("sony").first()
        assertEquals(1, cached.size)
        assertEquals("101", cached[0].id)
        assertEquals("Sony TV", cached[0].title)
        assertEquals(499.99, cached[0].price, 0.001)
        assertEquals("http://img1.jpg", cached[0].imageUrl)
    }

    @Test
    fun fetchAndCacheNextPage_failure_returnsFailureResult() = runTest(testDispatcher) {
        val exception = RuntimeException("API error")
        val fakeApi = object : WalmartApiService {
            override suspend fun searchProducts(keyword: String, page: Int, sortBy: String): WalmartSearchResponse {
                throw exception
            }
        }

        repository = ProductRepositoryImpl(fakeApi, dao, testDispatcher)

        val result = repository.fetchAndCacheNextPage("sony", 1)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
