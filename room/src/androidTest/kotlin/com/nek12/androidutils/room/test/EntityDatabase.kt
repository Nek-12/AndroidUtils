package com.nek12.androidutils.room.test

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.nek12.androidutils.room.RoomDao
import com.nek12.androidutils.room.RoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(tableName = Entry.TABLE_NAME)
data class Entry(
    @PrimaryKey(autoGenerate = false)
    override val id: String = UUID.randomUUID().toString(),
) : RoomEntity<String> {

    companion object {

        const val TABLE_NAME: String = "Entry"
    }
}

@Dao
abstract class EntryDao(db: RoomDatabase) : RoomDao<String, Entry>(db, Entry.TABLE_NAME) {

    @Query("SELECT * FROM ${Entry.TABLE_NAME}")
    abstract fun getAllDefault(): Flow<List<Entry>>
}

@Database(
    entities = [Entry::class],
    version = 1,
    exportSchema = false
)
abstract class EntryDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
}
