package com.nek12.androidutils.room

open class RoomRepo<T : RoomEntity>(private val dao: RoomDao<T>) {
    open suspend fun add(entity: T): Long = dao.add(entity)
    open suspend fun add(vararg entities: T) = dao.add(*entities)
    open suspend fun add(entities: List<T>) = dao.add(entities)
    open suspend fun update(entity: T) = dao.update(entity)
    open suspend fun update(entities: List<T>) = dao.update(entities)
    open suspend fun update(vararg entities: T) = dao.update(*entities)
    open suspend fun delete(entity: T) = dao.delete(entity)
    open suspend fun delete(vararg entities: T) = dao.delete(*entities)
    open suspend fun delete(entities: List<T>) = dao.delete(entities)
    open suspend fun deleteAll() = dao.deleteAll()
    open suspend fun getSync(id: Long): T? = dao.getSync(id)
    open suspend fun getAllSync(): List<T> = dao.getAllSync()
    open suspend fun getSync(ids: List<Long>): List<T> = dao.getSync(ids)
}
