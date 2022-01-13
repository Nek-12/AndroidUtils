package com.nek12.androidutils.room.test

import androidx.room.*
import com.nek12.androidutils.room.RoomDao
import com.nek12.androidutils.room.RoomEntity
import com.nek12.androidutils.room.RoomRepo

@Entity(tableName = Entry.TABLE_NAME)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,
) : RoomEntity {
    companion object {
        const val TABLE_NAME: String = "Entry"
    }
}

@Dao
abstract class EntryDao(db: RoomDatabase) : RoomDao<Entry>(db, Entry.TABLE_NAME)

class EntryRepo(dao: EntryDao) : RoomRepo<Entry>(dao)

@Database(
    entities = [Entry::class],
    version = 1
)
abstract class EntryDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
