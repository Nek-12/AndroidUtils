## Generic Room

`com.github.Nek-12.AndroidUtils:room:<version>`

> I'm tired of creating all those Entities, DAO's and Repositories. I want Room to generate CRUDs for my entities automatically.

No problem!

1. Your Entity:
   ```kotlin  
   @Entity(tableName = Entry.TABLE_NAME)  
   data class Entry(  
       // You still have to annotate everything properly!  
       @PrimaryKey(autoGenerate = true) 
       override val id: Long = 0,  
   ) : RoomEntity<Long> {  
       companion object {  
           const val TABLE_NAME: String = "Entry"  
       }  
   }  
   ```  
2. Your DAO:
   ```kotlin  
   //Room will inject db parameter automatically
   abstract class EntryDao(db: RoomDatabase) : RoomDao<Long, Entry>(db, Entry.TABLE_NAME)
   ```  
3. Your DataSource:
   ```kotlin  
   class EntryDataSource(private val dao: EntryDao) : RoomDataSource<Long, Entry>(dao) 
   ```  

You got 19 functions for free, including `add`, `delete`, `update`, and `get`

## Advanced Usage

Each DAO has a `referencedTables` parameter. This is used in `get(): Flow<T>` queries to trigger flow emission when any
of the `referencedTables` changes. For example, `@Embedded` entities.

By default, emissions are triggered when just the base table changes.

> What should Include in the `referencedTables` parameter?

Include all @Embedded tables, all `@Relation `tables, and all `ForeignKey` tables

## TBD

* Be aware that currently the library **does not support storing UUIDs** as primary keys. So if you decide to use
* it, please avoid blob UUIDs (UUID type argument in RoomEntity). I'm working on a fix right now.
