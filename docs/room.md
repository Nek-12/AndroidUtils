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
   ) : RoomEntity {  
       companion object {  
           const val TABLE_NAME: String = "Entry"  
       }  
   }  
   ```  
2. Your DAO:
   ```kotlin  
   abstract class EntryDao : RoomDao<Entry>(Entry.TABLE_NAME) {  
       @Query("SELECT * FROM ${Entry.TABLE_NAME}") 
       // You have to write async queries yourself
       abstract fun getAll(): Flow<List<Entry>>    
   }  
   ```  
3. Your Repo:
   ```kotlin  
   class EntryRepo(private val dao: EntryDao) : RoomRepo<Entry>(dao) {  
       fun getAll() = dao.getAll()  
   }  
   ```  

You got 15 functions for free, including `add`, `delete`, `update`, and `getSync` (suspending)

TBD:

* Generic async get() methods
