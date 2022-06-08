@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nek12.androidutils.room.test

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
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
            .enableMultiInstanceInvalidation() //may help with callbacks?
            .build()
        dao = db.entryDao()
        repo = EntryRepo(dao)
    }

    @After
    fun destroy() {
        db.close()
    }

    @Test
    fun testInvalidationSingleTable(): Unit = runTest(dispatchTimeoutMs = 5000) {

        dao.getAll().test {
            assertEquals(0, awaitItem().size)
            dao.add(Entry())
            val items = awaitItem()
            assertEquals(1, items.size)
            dao.delete(items.first().id)
            assertEquals(0, awaitItem().size)
            expectNoEvents()
        }
    }


    @Test
    fun testOperations(): Unit = runTest {
        val entities = (1..10).map { Entry() }
        // multiple
        await { dao.add(entities) }
        assertEquals(10, dao.getAllSync().count())

        await { dao.add(entities.first()) }
        assertEquals(10, dao.getAllSync().count())

        assertEquals(entities.size, dao.getSync(entities.map { it.id }).count())

        await { dao.delete(entities.map { it.id }) }

        assertEquals(0, dao.getAllSync().count())

        val entity = Entry()
        await { dao.add(entity) }
        assertEquals(1, dao.getAllSync().count())

        await { dao.delete(entity.id) }

    }

    private inline fun TestScope.await(call: () -> Unit) {
        call()
        advanceUntilIdle()
    }
}
