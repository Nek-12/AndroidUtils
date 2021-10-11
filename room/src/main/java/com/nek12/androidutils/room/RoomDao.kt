package com.nek12.androidutils.room

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Provides insert,update, delete, and getSync queries for you.
 * Implement get(): LiveData<T> queries yourself (still not possible with Room)
 **/
@Dao
abstract class RoomDao<T : RoomEntity>(private val tableName: String) {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun add(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun add(vararg entities: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
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

    @RawQuery
    protected abstract suspend fun deleteAll(query: SupportSQLiteQuery): Int

    suspend fun deleteAll() {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName")
        deleteAll(query)
    }

    @RawQuery
    protected abstract suspend fun getSync(query: SupportSQLiteQuery): List<T>?

    suspend fun getSync(id: Long): T? {
        return getSync(listOf(id)).firstOrNull()
    }

    suspend fun getAllSync(): List<T> {
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName ;")
        return getSync(query) ?: emptyList()
    }

    suspend fun getSync(ids: List<Long>): List<T> {
        val result = StringBuilder()
        for (index in ids.indices) {
            if (index != 0) {
                result.append(",")
            }
            result.append("'").append(ids[index]).append("'")
        }
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id IN ($result);")
        return getSync(query) ?: emptyList()
    }
}
