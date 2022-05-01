@file:Suppress("unused")

package com.nek12.androidutils.room

import kotlinx.coroutines.flow.Flow

/**
 * A datasource class that uses the generated [RoomDao] from the library.
 * Example
 * ```
 * class EntryDataSource(
 *     private val dao: EntryDao
 * ): RoomDataSource<Long, Entry>(dao)
 *
 * ```
 *
 * @see RoomDao
 * @see RoomEntity
 */
open class RoomDataSource<I: Any, T: RoomEntity<I>>(private val dao: RoomDao<I, T>) {

    open suspend fun add(entity: T) = dao.add(entity)
    open suspend fun add(vararg entities: T) = dao.add(*entities)
    open suspend fun add(entities: List<T>) = dao.add(entities)

    open suspend fun update(entity: T) = dao.update(entity)
    open suspend fun update(entities: List<T>) = dao.update(entities)
    open suspend fun update(vararg entities: T) = dao.update(*entities)

    open suspend fun delete(entity: T) = dao.delete(entity)
    open suspend fun delete(vararg entities: T) = dao.delete(*entities)
    open suspend fun delete(entities: List<T>) = dao.delete(entities)
    open suspend fun delete(id: I) = dao.delete(id)

    @JvmName("deleteByIds")
    @Suppress("INAPPLICABLE_JVM_NAME") //TODO: Java incompatibility
    open suspend fun delete(ids: List<I>) = dao.delete(ids)
    open suspend fun delete(vararg ids: I) = dao.delete(*ids)

    open suspend fun deleteAll() = dao.deleteAll()

    open suspend fun getSync(id: I): T? = dao.getSync(id)
    open suspend fun getSync(vararg ids: I): List<T> = dao.getSync(*ids)
    open suspend fun getSync(ids: List<I>): List<T> = dao.getSync(ids)
    open suspend fun getAllSync(): List<T> = dao.getAllSync()

    open fun get(id: I): Flow<T?> = dao.get(id)
    open fun get(ids: List<I>): Flow<List<T>> = dao.get(ids)
    open fun get(vararg ids: I): Flow<List<T>> = dao.get(*ids)
    open fun getAll(): Flow<List<T>> = dao.getAll()
}
