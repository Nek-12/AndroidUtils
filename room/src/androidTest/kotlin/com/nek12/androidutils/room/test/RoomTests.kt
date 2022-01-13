@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nek12.androidutils.room.test

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomTests {
    private lateinit var db: EntryDatabase
    private lateinit var dao: EntryDao
    private lateinit var repo: EntryRepo


    companion object {
        private val testDispatcher by lazy { StandardTestDispatcher() }
        private val testScope by lazy { TestScope(testDispatcher) }

        @JvmStatic
        @BeforeClass
        fun prepare() {
            Dispatchers.setMain(testDispatcher)
        }

        @JvmStatic
        @AfterClass
        fun cleanup() {
            Dispatchers.resetMain()
        }
    }

    @Before
    fun init() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, EntryDatabase::class.java
        )
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        dao = db.entryDao()
        repo = EntryRepo(dao)
    }

    @After
    fun destroy() {
        db.close()
    }

    @Test
    fun testInvalidationSingleTable(): Unit = testScope.runTest(5000) {

        val job = async {
            dao.getAll()
                .onEach { println(it) }
                .take(3)
                .toList()
        }
        advanceUntilIdle()
        dao.add(Entry())
        advanceUntilIdle()
        dao.add(Entry())
        advanceUntilIdle()
        val expected = listOf(0, 1, 2)
        val result = job.await()
        println(result)
        assertEquals(expected, result.map { it.size })
    }
}
