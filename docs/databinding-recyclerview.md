## Databinding RecyclerView

```kotlin
implementation("com.github.Nek-12.AndroidUtils:databinding-recyclerview:$utilsVersion")
```

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

Simple adapter does not provide custom binding logic or accessing your binding inside itemClickListener, so let's use
SingleTypeAdapter

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

### ItemBuilder

Your two biggest pains are now creating and supplying items. While creating items is up to you, supplying items is easy
because of ItemBuilder class. Instead of calling a bunch of functions and building a list with several item types
yourself, use it like this:

```kotlin
ItemBuilder()
    .header(R.string.top_items_title, R.layout.item_header)
    .items(listOfItems.take(10))
    .header(R.string.worst_items_title, R.layout.item_header)
    .items(listOfItems.takeLast(10))
    .blank(R.layout.item_spacer)
    .build()
```

Or you can go for declarative style:

```kotlin
val readyToSubmitItems = ItemBuilder {
    items(severalItems)
    header(R.string.innocent_items_title)
    filtered(severalSuspiciousItems) { !it.isSuspicious }
    data(rawDataList, R.layout.my_mapped_item, { it.id }) {
        it.binding.textView.text = it.item.text
    }
    blank(R.layout.footer)
}
```

### ItemClickListener

1. Let your activity/fragment implement ItemClickListener / ItemLongClickListener or ItemInflateListener, or all of these.
2. Then set the listener when you create an adapter. Depending on the type of the listener, it will be called at
   appropriate times. The listener is called not only when the user presses "the background" (as it would have if you
   used regular cheap RecyclerView, but when the user presses **any** part of the view, and as a parameter, you will get
   an item, and a view id, using which you can determine what kind of view was clicked on. For example, when the user
   clicks on the checkbox, you will get your checkbox's view id, when background - you will get the root layout id.)
3. Handle item types, view types and position in the listener's function. Don't worry about view lifecycle.

```kotlin
class OverviewFragment : RVDataBindingFragment<FragmentOverviewBinding>(),
    ItemClickListener<Item<*, *>>

override fun onItemClicked(view: View, item: Item<*, *>, pos: Int) {
    when (item) {
        //This is why you needed sealed classes in the guide above
        is OverviewItem.Entry -> {
            when (view.id) {
                R.id.edit -> editInstance(item.id)
                R.id.start -> navigateToDetails(pos)
                R.id.remove -> deleteInstance(item) //Automatically cast to Entry
                R.id.entry_check_box -> onHabitChecked(pos, view as CheckBox, item)
                else -> {}
            }
        }
        is OverviewItem.Summary -> {
            /* Do not react to clicks */
        }
    }
}
```

TBD:
* Item diff payloads
* Item decorations
