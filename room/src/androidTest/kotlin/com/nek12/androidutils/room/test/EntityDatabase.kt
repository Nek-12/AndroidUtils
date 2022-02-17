package com.nek12.androidutils.room.test

import androidx.room.*
import com.nek12.androidutils.room.RoomDao
import com.nek12.androidutils.room.RoomDataSource
import com.nek12.androidutils.room.RoomEntity
import java.util.*

@Entity(tableName = Entry.TABLE_NAME)
data class Entry(
    @PrimaryKey(autoGenerate = false)
    override val id: UUID = UUID.randomUUID(),
) : RoomEntity<UUID> {

    companion object {

        const val TABLE_NAME: String = "Entry"
    }
}

@Dao
abstract class EntryDao(db: RoomDatabase) : RoomDao<UUID, Entry>(db, Entry.TABLE_NAME)

class EntryRepo(dao: EntryDao) : RoomDataSource<UUID, Entry>(dao)

@Database(
    entities = [Entry::class],
    version = 1,
    exportSchema = false
)
abstract class EntryDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}
