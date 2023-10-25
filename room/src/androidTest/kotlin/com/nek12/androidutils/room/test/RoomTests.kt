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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomTests {

    private lateinit var db: EntryDatabase
    private lateinit var dao: EntryDao

    @Before
    fun init() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, EntryDatabase::class.java
        )
            .setTransactionExecutor(dispatcher.asExecutor())
            .setQueryExecutor(dispatcher.asExecutor())
            .build()
        dao = db.entryDao()
    }

    @After
    fun destroy() {
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun testInvalidationSingleTable(): Unit = runTest(timeout = 5000.milliseconds) {

        dao.getAllDefault().test {
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
    fun testOperations(): Unit = runTest(timeout = 5000.milliseconds) {
        val entities = (1..10).map { Entry() }
        // multiple
        await { dao.add(entities) }
        assertEquals(10, dao.getAllSync().count())

        await { dao.save(entities.first()) }
        assertEquals(10, dao.getAllSync().count())

        assertEquals(entities.size, dao.getSync(entities.map { it.id }).count())

        await { dao.delete(entities.map { it.id }) }

        assertEquals(0, dao.getAllSync().count())

        val entity = Entry()
        await { dao.add(entity) }
        assertEquals(1, dao.getAllSync().count())

        await { dao.save(Entry()) }
        assertEquals(2, dao.getAllSync().count())
    }

    private inline fun TestScope.await(call: () -> Unit) {
        call()
        advanceUntilIdle()
    }
}
