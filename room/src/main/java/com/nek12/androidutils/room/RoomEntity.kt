package com.nek12.androidutils.room

/**
 * An abstract RoomEntity. Extend this class to be able to generate CRUDS for your DAOs and repos
 * automatically. You can use dataclasses to implement the entity most quickly.
 * **You still have to annotate overriden fields properly.**
 *  Example:
 *  ```
 *  @Entity(tableName = Entry.TABLE_NAME)
 *      data class Entry(
 *      val title: String,
 *      @PrimaryKey(autoGenerate = true)
 *      override val id: Long = 0
 *  ) : RoomEntity {
 *      companion object {
 *          const val TABLE_NAME = "entry"
 *      }
 *  }
 * ```
 * @see RoomRepo
 * @see RoomDao
 */
interface RoomEntity {
    val id: Long
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}
