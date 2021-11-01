# AndroidUtils
![GitHub](https://img.shields.io/github/license/Nek-12/AndroidUtils)
![GitHub last commit](https://img.shields.io/github/last-commit/Nek-12/AndroidUtils)
![Maintenance](https://img.shields.io/maintenance/yes/2021)
[![Downloads on Jitpack](https://jitpack.io/v/Nek-12/AndroidUtils/month.svg)](https://jitpack.io/#Nek-12/AndroidUtils.svg)

Latest version is  [![Jitpack Version](https://jitpack.io/v/Nek-12/AndroidUtils.svg)](https://jitpack.io/#Nek-12/AndroidUtils) 

Extensions available:
```kotlin
val utilsVersion = "<look up 👆🏻>"
implementation ("com.github.Nek-12.AndroidUtils:recyclerview:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:preferences-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:genericpagingadapter:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:databinding:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:core-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:android-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:safenavcontroller:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:coroutine-ktx:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:room:$utilsVersion")
implementation ("com.github.Nek-12.AndroidUtils:material-ktx:$utilsVersion")
```  

## Databinding RecyclerView

`com.github.Nek-12.AndroidUtils:recyclerview:<version>`

If you're using databinding in your project, with this library you can forget about writing  
adapters, viewholders and itemtouchhelpers for each of your screens, over and over.

> "I want a simple list and I will write no more than 2 lines of code!"

No problem:

1. Your adapter:
   ```kotlin
   private val adapter = SimpleAdapter<String>(R.layout.item_title, itemClickListener)  
   ```    

2. Submitting data:
    ```kotlin
    val result = listOf("Chicken", "Meat", "Milk")    
    //We do not have a good ID here, so we can just use null or Item.NO_ID    
    adapter.submitData(result) { null }   
    ```  

3. Bind your data in the XML
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <layout
           xmlns:android="http://schemas.android.com/apk/res/android"
           xmlns:app="http://schemas.android.com/apk/res-auto">

       <data>
           <variable name="data"  type="String"/>
       </data>

       <TextView
           android:id="@+id/text"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"
           android:text="@{data}"/>
   </layout>
   ```

Two lines of code ✅

> But I want to have custom binding logic for my items!

Simple adapter does not provide custom binding logic or accessing your binding inside itemClickListener, so let's use SingleTypeAdapter

1. Adapter:
   ```kotlin 
   val adapter = SingleTypeAdapter<CheckBoxData>(R.layout.item_checkbox, itemClickListener) {    
     it.binding.checkBox.isChecked = viewModel.isCached(it.data)  
   }  
   ```  
2. Submitting data:
   ```kotlin  
   viewModel.boxesFlow.collectOnLifecycle(viewLifecycleOwner) { data ->   
       adapter.submitData(data) { it.id } //or null if you have nothing to serve as an id  
   }  
   ```  
Three lines of code ✅

> But I have super-robust binding logic, several types of Items, headers, footers, separators, want custom diff calculation or just want to move my item creation logic to a ViewModel!

No problem, here's how you do it:
1. Your adapter:
   ```kotlin  
   sealed class MainMenuItem<T, VB : ViewDataBinding> : Item<T, VB>() {  
       data class Entry(  
           override val data: MenuEntryEntity,  
       ) : MainMenuItem<MenuEntryEntity, ItemMainMenuEntryBinding>() {  
           override val layout: Int get() = R.layout.item_main_menu  
           override val id: Long get() = data.id  
           //data field is used when calculating diff in equals()  
  
           override fun bind(binding: ItemMainMenuEntryBinding, bindingPos: Int) {  
               if (data.isAGoodDay && bindingPos == 0) binding.sadSmiley.hide()  
           }  
       }  
         
       // A rather rare case where you have a completely fixed layout, but still want to have a separate item.  
       // Why? You want to handle clicks, most likely, in your itemClickListener.   
       // Use GenericItem when you don't.  
       class Header : MainMenuItem<Unit, ItemMainMenuHeaderBinding>() {  
           override val data: Unit = Unit  
           override val id = NO_ID  
           override val layout = R.layout.item_main_menu_header  
           override fun equals(other: Any?): Boolean = false //always rebind  
           override fun hashCode(): Int = layout.hashCode()  
       }  
  
       data class SubHeader(  
           override val data: String  
       ) : MainMenuItem<String, ItemSubHeaderBinding>() {  
           override val id = NO_ID  
           override val layout = R.layout.item_main_menu_category  
       }  
   }   
   ```  
Not exactly three lines, but much less code than you would write otherwise ✅

> But I'm using paging library and your adapter does not suit me!

No problem!
1. Add dependency `com.github.Nek-12.AndroidUtils:genericpagingadapter:<version>`
2. Your adapter:
   ```kotlin  
   private val adapter = GenericPagingAdapter()  
   ```  
3. Your Items:
   ```kotlin  
   val entries = Pager(PagingConfig()) {  
       entryRepo.getAllPaginated()  
   }.flow.cachedIn(viewModelScope).mapLatest { pagingData ->  
       //EntryItem sealed class is implemented elsewhere as shown above  
       pagingData.map { EntryItem.TimelineItem(it) }.insertSeparators { before, after ->  
           when {  
               before == null -> null  
               after == null -> null  
               else -> EntryItem.SeparatorItem()  
           }  
       }  
   }  
   ```  
4. Submitting data:
   ```kotlin  
   viewModel.entries.collectOnLifecycle(viewLifecycleOwner) { items ->  
       adapter.submitData(items)  
   }  
   ```  

TBD:
* Item diff payloads
* Item decorations

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

## Other components
Documentation on those is still TBD, however there is not much code in them, so you can check out sources or javadocs if you want more.
* `***-ktx` artifacts will give you some useful extension functions like `collectOnLifecycle()` that I used in examples above to simplify   
  working with system APIs, coroutines, and other android components.
* `SafeNavController` - will give you a class to replace your NavController that you use with navigation library, because it has one huge flaw: The Dreaded "Destination Not Found" Exception. To avoid crashing your app at runtime, use `Fragment.findSafeNavController()` instead of `Fragment.findNavController()` and use provided methods just like you would use the usual controller.
* `databinding` - will give you a generic DataBindingFragment class implementation. Super useful if you use `recyclerview` or databinding already. Extend that class and override your layout id. No need to null out binding, inflate anything, just initialize your fragment logic in the `onViewReady()`.
* `preferences-ktx `- will give you property delegates that automatically read data from shared prefs and write to them. Use them wisely because they still do I/O on the main thread.
* `core-ktx` - Will give you a Time class implementation that I used in one of my projects, because there is still no analogue on the internet. If you need to manipulate time values efficiently or store time in the database (supported by `room` extension `DatabaseConverters` class by the way), then use `Time`. This artifact has literally zero dependencies, and does not depend on any android parts, actually.

For more information and other examples see javadocs in the library code.
If you find something that is missing, feel free to tell me about it using Github issues.

* [This Medium Post](https://medium.com/@berryhuang/android-room-generic-dao-27cfc21a4912) inspired me to create a generic DAO implementation.
* [This Medium Article](https://medium.com/android-news/using-databinding-like-a-pro-to-write-generic-recyclerview-adapter-f94cb39b65c4) inspired me to create my `recyclerview` implementation
* Other extensions, tricks, classes and ideas were inspired by open-source community: Medium posts, StackOverflow answers, other libraries and so on. Thanks to everyone for such a valuable information!


## License
```
   Copyright 2021 Nikita Vaizin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```
