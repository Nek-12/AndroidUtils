package com.nek12.androidutils.room

import android.annotation.SuppressLint
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.*

/**
 * A generic dao class that provides CRUD methods for you for free.
 * Extend this class to add your own methods.
 * Provides insert, update, delete, and get queries for you.
 * @param db Just add this parameter to your DAO and pass it along to the RoomDao base constructor.
 *   This parameter is an implementation detail and is handled by Room codegen.
 *   example
 *   ```
 *   abstract class MyDao(db: RoomDatabase) : RoomDao<MyEntity>(db, MyEntity.TABLE_NAME)
 *   ```
 * @param referencedTables A list of tables that your [T] entity references. For example, [Embedded] entities.
 *   This is used in `get(): Flow<T>` queries to trigger flow emission when any of the [referencedTables] changes.
 *   By default, emissions are triggered when just the [tableName] table changes
 * @see RoomEntity
 * @see RoomDataSource
 **/
@Dao
abstract class RoomDao<I : Any, T : RoomEntity<I>>(
    private val db: RoomDatabase,
    private val tableName: String,
    private val referencedTables: Array<String> = arrayOf(tableName),
) {

    /**
     * @return The id of a newly-inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(vararg entities: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(entities: List<T>)

    @Update
    abstract suspend fun update(entity: T)

    @Update
    abstract suspend fun update(entities: List<T>)

    @Update
    abstract suspend fun update(vararg entities: T)

    @Delete
    abstract suspend fun delete(entity: T)

    @Delete
    abstract suspend fun delete(vararg entity: T)

    @Delete
    abstract suspend fun delete(entities: List<T>)

    /**
     * @return How many items were deleted
     */
    @JvmName("deleteById")
    suspend fun delete(ids: List<I>): Int {
        val idsQuery = buildSqlIdList(ids)
        val query = SimpleSQLiteQuery("DELETE FROM `$tableName` WHERE `id` IN ($idsQuery);")
        return delete(query)
    }

    /**
     * @return How many items were deleted
     */
    suspend fun delete(vararg ids: I): Int {
        return delete(ids.toList())
    }

    /**
     * @return How many items were deleted
     */
    suspend fun deleteAll(): Int {
        val query = SimpleSQLiteQuery("DELETE FROM `$tableName`;")
        return delete(query)
    }

    /**
     * Get an entity synchronously (suspending)
     */
    suspend fun getSync(id: I): T? {
        return getSync(listOf(id)).firstOrNull()
    }

    suspend fun getSync(vararg ids: I): List<T> {
        return getSync(ids.toList())
    }

    suspend fun getAllSync(): List<T> {
        val query = SimpleSQLiteQuery("SELECT * FROM `$tableName`;")
        return getSync(query) ?: emptyList()
    }

    suspend fun getSync(ids: List<I>): List<T> {
        return getSync(buildSqlIdQuery(ids)) ?: emptyList()
    }

    /**
     * Use [Flow.distinctUntilChanged] to prevent duplicate emissions when unrelated entities are changed
     * Re-emits values when any of the [referencedTables] change
     */
    fun get(id: I): Flow<T?> {
        return createFlow { getSync(id) }
    }

    /**
     * Use [Flow.distinctUntilChanged] to prevent duplicate emissions when unrelated entities are changed
     * Re-emits values when any of the [referencedTables] change
     */
    fun get(vararg ids: I): Flow<List<T>> {
        return get(ids.toList())
    }

    /**
     * Use [Flow.distinctUntilChanged] to prevent duplicate emissions when unrelated entities are changed
     * Re-emits values when any of the [referencedTables] change
     */
    fun get(ids: List<I>): Flow<List<T>> {
        return createFlow { getSync(ids) }
    }

    /**
     * Use [Flow.distinctUntilChanged] to prevent duplicate emissions when unrelated entities are changed.
     * Re-emits values when any of the [referencedTables] change
     */
    fun getAll(): Flow<List<T>> {
        return createFlow { getAllSync() }
    }

    @RawQuery
    protected abstract suspend fun delete(query: SupportSQLiteQuery): Int

    @RawQuery
    protected abstract suspend fun getSync(query: SupportSQLiteQuery): List<T>?

    @SuppressLint("RestrictedApi")
    private fun buildSqlIdList(ids: List<I>): String {
        return buildString {
            ids.forEachIndexed { i, id ->
                //TODO: Support UUID blobs
                if (i != 0) {
                    append(",")
                }
                append("'$id'")
            }
        }
    }

    private fun buildSqlIdQuery(ids: List<I>): SimpleSQLiteQuery {
        val idsQ = buildSqlIdList(ids)
        return SimpleSQLiteQuery("SELECT * FROM $tableName WHERE `id` IN ($idsQ);")
    }

    /**
     * A solution to dynamically subscribe the flow of entities to updates in the database.
     * Uses invalidation tracker to force to re-query the database when the database is updated
     * (calls the onInvalidated() lambda)
     * Used because @RawQuery doesn't support generic type parameters
     * The channel will post whenever one of the [referencedTables] is changed
     * Source partially based on [CoroutinesRoom.createFlow] source code
     */
    private inline fun <R> createFlow(
        crossinline onInvalidated: suspend () -> R,
    ): Flow<@JvmSuppressWildcards R> = flow {
        coroutineScope {
            // Observer channel receives signals from the invalidation tracker to emit queries.
            val observerChannel = Channel<Unit>(Channel.CONFLATED)
            val observer = object : InvalidationTracker.Observer(referencedTables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    observerChannel.trySend(Unit)
                }
            }
            observerChannel.trySend(Unit) // Initial signal to perform first query.
            //TODO: Wait for a normal api to get transactionDispatcher from room devs
            val queryContext =
                if (db.inTransaction()) db.transactionExecutor.asCoroutineDispatcher() else db.getQueryDispatcher()
            val resultChannel = Channel<R>()
            launch(queryContext) {
                db.invalidationTracker.addObserver(observer)
                try {
                    // Iterate until cancelled, transforming observer signals to query results
                    // to be emitted to the flow.
                    for (signal in observerChannel) {
                        val result = onInvalidated()
                        resultChannel.send(result)
                    }
                } finally {
                    db.invalidationTracker.removeObserver(observer)
                }
            }
            emitAll(resultChannel)
        }
    }

    //TODO: Room createFlow() does not provide an API to pass a suspending function to be executed.
    //  Therefore, callables that CAN be passed won't work for our purposes - we need a suspending call to retrieve data
    //  Not using provided implementation and instead using copy-pasted code with few simple additions.
    //  Although not using provided api to get TransactionExecutor poses bug disaster by circumventing normal transaction dispatchers
    //  (which are internal)
}

private fun UUID.toByteBlob(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}

private fun ByteArray.toUUID(): UUID {
    val bb: ByteBuffer = ByteBuffer.wrap(this)
    val mostSignificantBits = bb.long
    val leastSignificantBits = bb.long
    return UUID(mostSignificantBits, leastSignificantBits)
}
