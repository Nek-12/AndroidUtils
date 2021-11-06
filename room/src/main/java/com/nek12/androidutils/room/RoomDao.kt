package com.nek12.androidutils.room

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * A generic dao class that provides CRUD methods for you for free.
 * Extend this class to add your own methods.
 * Provides insert,update, delete, and getSync queries for you.
 * Implement async queries like `get(): LiveData<T>` or `get(): Flow<T>` yourself
 * Example:
 * ```
 * @Dao
 * abstract class EntryDao : RoomDao<Entry>(Entry.TABLE_NAME) {
 *     @Query("SELECT * FROM ${Entry.TABLE_NAME}")
 *     abstract fun getAll(): Flow<List<Entry>>
 * ```
 * @see RoomEntity
 * @see RoomRepo
 **/
@Dao
abstract class RoomDao<T : RoomEntity>(private val tableName: String) {
    /**
     * @return The id of a newly-inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(entity: T): Long

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

    suspend fun deleteById(id: Long) {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName WHERE id = ($id);")
        delete(query)
    }

    /**
     * @return How many items were deleted
     */
    suspend fun deleteById(ids: List<Long>): Int {
        val idsQuery = buildSqlIdList(ids)
        val query = SimpleSQLiteQuery("DELETE FROM $tableName WHERE id IN ($idsQuery);")
        return delete(query)
    }

    /**
     * @return How many items were deleted
     */
    suspend fun deleteById(vararg ids: Long): Int {
        return deleteById(ids.toList())
    }

    /**
     * @return How many items were deleted
     */
    suspend fun deleteAll(): Int {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName")
        return delete(query)
    }

    /**
     * Get an entity synchronously (suspending)
     */
    suspend fun getSync(id: Long): T? {
        return getSync(listOf(id)).firstOrNull()
    }

    suspend fun getSync(vararg ids: Long): List<T> {
        return getSync(ids.toList())
    }

    suspend fun getAllSync(): List<T> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName;")
        return getSync(query) ?: emptyList()
    }

    suspend fun getSync(ids: List<Long>): List<T> {
        val idsQuery = buildSqlIdList(ids)
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id IN ($idsQuery);")
        return getSync(query) ?: emptyList()
    }

    @RawQuery
    protected abstract suspend fun delete(query: SupportSQLiteQuery): Int

    @RawQuery
    protected abstract suspend fun getSync(query: SupportSQLiteQuery): List<T>?

    private fun buildSqlIdList(ids: List<Long>): String {
        val result = StringBuilder()
        for (index in ids.indices) {
            if (index != 0) {
                result.append(",")
            }
            result.append("'").append(ids[index]).append("'")
        }
        return result.toString()
    }
}
