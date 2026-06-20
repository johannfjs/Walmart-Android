package com.johannjara.walmart.data.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johannjara.walmart.data.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SearchHistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SearchHistoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.searchHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSearchHistory() = runTest {
        val entry = SearchHistoryEntity(queryText = "nintendo", timestamp = 1000L)
        dao.insert(entry)
        val history = dao.getSearchHistory().first()
        assertEquals(1, history.size)
        assertEquals("nintendo", history[0].queryText)
        assertEquals(1000L, history[0].timestamp)
    }

    @Test
    @Throws(Exception::class)
    fun insertDuplicateReplacesAndUpdatesTimestamp() = runTest {
        val entry1 = SearchHistoryEntity(queryText = "nintendo", timestamp = 1000L)
        dao.insert(entry1)
        val entry2 = SearchHistoryEntity(queryText = "nintendo", timestamp = 2000L)
        dao.insert(entry2)

        val history = dao.getSearchHistory().first()
        assertEquals(1, history.size)
        assertEquals("nintendo", history[0].queryText)
        assertEquals(2000L, history[0].timestamp)
    }

    @Test
    @Throws(Exception::class)
    fun getSearchHistoryLimitsTo10OrderedByTimestampDesc() = runTest {
        for (i in 1..12) {
            val entry = SearchHistoryEntity(queryText = "query_$i", timestamp = i * 1000L)
            dao.insert(entry)
        }

        val history = dao.getSearchHistory().first()
        assertEquals(10, history.size)
        assertEquals("query_12", history[0].queryText)
        assertEquals("query_3", history[9].queryText)
    }
}
