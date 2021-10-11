package com.nek12.androidutils.room

abstract class RoomEntity {
    abstract val id: Long
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}
