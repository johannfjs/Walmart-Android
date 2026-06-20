package com.johannjara.walmart.data.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johannjara.walmart.data.entity.ProductCacheEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProductCacheDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductCacheDao

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
    fun insertAndGetProductsByKeyword() = runTest {
        val products = listOf(
            ProductCacheEntity("1", "sony", "Sony TV", 499.99, "img1.jpg", 1),
            ProductCacheEntity("2", "sony", "Sony Phone", 299.99, "img2.jpg", 1),
            ProductCacheEntity("3", "xbox", "Xbox Console", 399.99, "img3.jpg", 1)
        )
        dao.insertProducts(products)

        val sonyProducts = dao.getCachedProductsStream("sony").first()
        assertEquals(2, sonyProducts.size)
        assertEquals("Sony TV", sonyProducts[0].title)
        assertEquals("Sony Phone", sonyProducts[1].title)

        val xboxProducts = dao.getCachedProductsStream("xbox").first()
        assertEquals(1, xboxProducts.size)
        assertEquals("Xbox Console", xboxProducts[0].title)
    }

    @Test
    fun getProductsSortedByPageAndId() = runTest {
        val products = listOf(
            ProductCacheEntity("b", "sony", "B Product", 20.0, "img2.jpg", 2),
            ProductCacheEntity("a", "sony", "A Product", 10.0, "img1.jpg", 2),
            ProductCacheEntity("c", "sony", "C Product", 30.0, "img3.jpg", 1)
        )
        dao.insertProducts(products)

        val result = dao.getCachedProductsStream("sony").first()
        assertEquals(3, result.size)
        // sorted by cached_at_page ASC, then id ASC
        assertEquals("c", result[0].id) // page 1
        assertEquals("a", result[1].id) // page 2, id a
        assertEquals("b", result[2].id) // page 2, id b
    }

    @Test
    fun deleteProductsByKeyword() = runTest {
        val products = listOf(
            ProductCacheEntity("1", "sony", "Sony TV", 499.99, "img1.jpg", 1),
            ProductCacheEntity("2", "xbox", "Xbox Console", 399.99, "img3.jpg", 1)
        )
        dao.insertProducts(products)

        dao.deleteProductsByKeyword("sony")

        val sonyProducts = dao.getCachedProductsStream("sony").first()
        assertEquals(0, sonyProducts.size)

        val xboxProducts = dao.getCachedProductsStream("xbox").first()
        assertEquals(1, xboxProducts.size)
    }
}
