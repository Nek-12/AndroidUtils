@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.databinding.recyclerview.Item.Companion.NO_ID

internal const val CAST_MESSAGE = """
    Could not bind your Item because you provided wrong layout|type argument pair
"""

internal const val TAG = "GenericAdapter"

/**
 * A lambda that is going to be called when the ViewHolder is rebound.
 * You can put here all the logic that you usually put into [Item.bind]
 */
typealias RVBinder<T, VB> = (BindPayload<T, VB>) -> Unit

/**
 * Use [Item]s to populate your generic recyclerview.
 * Items serve the purpose of viewHolders, adapter inner logic and encapsulate your business
 * logic. If you want a simple list with just one item type and zero custom binding logic, save
 * yourself some time and use [SimpleAdapter] and [GenericItem].
 *
 * **If you provide a wrong [layout] <-> [VB] argument pair or do not implement the [data] variable
 * in your xml properly, your app will crash at runtime or data will not get bound.**
 *
 * If you have a single view type but want to have custom binding logic, use [SingleTypeAdapter]
 * and [GenericItem] together. If you have more than one type, however, you have to implement a
 * sealed class and use [GenericAdapter].
 *
 * ### [data]
 * Whatever data that you want to be passed to your xml. **Remember that your XML
 * databinding parameter must have a variable named exactly "data" and it must have the type of
 * your [T] type parameter!** For more info see [BaseHolder]
 *
 * It can be whatever class you choose, though I recommend mapping your
 * entities explicitly to data classes to allow saving dynamic state and even two-way binding,
 * and to skip on implementing [equals] and [hashCode] yourself.
 *
 * You can also make [Item] a dataclass if you want. This way your [data] field will be compared in the [equals]
 * which is convenient.
 *
 * ### [id]
 * A **UNIQUE** identifier for the Item that you are trying to display. Your
 * recyclerview performance depends on how you override this parameter. You can use [NO_ID] in those
 * cases when your items are so simple you have nothing to represent an id for them, but do not
 * abuse that functionality as you degrade your list's performance, and **never** user non-unique IDs or you'll get
 * crashes in runtime
 *
 * You can use different [T] and [VB] types to implement multiple item type lists as follows:
 * Example:
 * ```
 * sealed class MainMenuItem<T, VB : ViewDataBinding> : Item<T, VB>() {
 *    data class Entry(
 *       override val data: MenuEntryEntity,
 *    ) : MainMenuItem<MenuEntryEntity, ItemMainMenuEntryBinding>() {
 *        override val layout: Int get() = R.layout.item_main_menu
 *        override val id: Long get() = data.id
 *        //data field is used in equals() and hashcode, no need to override
 *
 *        override fun bind(binding: ItemMainMenuEntryBinding, bindingPos: Int) {
 *            //use data, layout and binding together
 *        }
 *    }
 *
 *    data class Header(
 *       override val data: Unit, //don't forget to use something else in equals()
 *    ) : MainMenuItem<Unit, ItemMainMenuHeaderBinding>() { /* blah */  }
 * }
 * ```
 */
abstract class Item<T, in VB : ViewDataBinding> {

    /**
     * The data that will be passed to the xml as a "data" variable
     * **Remember that your XML
     * databinding parameter must have a variable named exactly "data" and it must have the type of
     * your [T] type parameter!**
     */
    abstract val data: T

    /**
     * If your data has nothing to use as an id, you can use [NO_ID].
     * You must either set a **unique** id or [NO_ID], or you'll get crashes at runtime!
     */
    abstract val id: Long

    /**
     * Needed for diff calculation. You can use data classes to provide this field for you.
     * It is recommended that you compare your [data] fields here.
     */
    abstract override fun equals(other: Any?): Boolean

    /**
     * Needed for diff calculation. You can use your [data] field's hashcode
     */
    abstract override fun hashCode(): Int

    @get:LayoutRes
    abstract val layout: Int

    /**
     * Override this function to put your custom binding logic here.
     */
    open fun bind(binding: VB, bindingPos: Int) {}

    internal fun tryBind(binding: ViewDataBinding, bindingPos: Int) = bind(
        requireNotNull(cast(binding)) { CAST_MESSAGE },
        bindingPos
    )

    @Suppress("UNCHECKED_CAST")
    private fun cast(viewDataBinding: ViewDataBinding): VB? = viewDataBinding as? VB

    companion object {
        /**
         * A value representing that this [Item] has no unique [id]
         */
        const val NO_ID = RecyclerView.NO_ID

        fun <T, VB : ViewDataBinding> itemFromData(
            item: T, id: Long?,
            @LayoutRes layout: Int,
            binder: RVBinder<T, VB>?
        ): GenericItem<T, VB> = GenericItem(item, id ?: NO_ID, layout, binder)
    }
}

/**
 * This is the item that has no data, just a layout, like a header, a separator or a footer.
 * The performance of this Item is degraded down to practically zero difference from not using
 * any optimizaations. [equals] always returns false, and [layout] is used instead during [id]
 * comparison, though these items are usually incredibly lightweight.
 * @param alwaysRebound Whether this item should be rebound on **every** diffing pass. If false,
 * the item is **never** rebound.
 */
data class BlankItem(
    @LayoutRes override val layout: Int,
    val alwaysRebound: Boolean = false
) : Item<Unit, ViewDataBinding>() {
    override val data: Unit get() = Unit
    override val id: Long get() = RecyclerView.NO_ID
    override fun equals(other: Any?): Boolean = !alwaysRebound
    override fun hashCode(): Int = 31 * layout + 29 * if (alwaysRebound) 1 else 0
}

/**
 * An item that does not require for you to override the [Item] class. You can use this
 * class directly instead of Item in simple cases, just do not abuse it, because it breaks
 * encapsulation.
 * Most useful for use with [SimpleAdapter] and [SingleTypeAdapter]
 */
data class GenericItem<T, VB : ViewDataBinding>(
    override val data: T,
    override val id: Long,
    override val layout: Int,
    val binder:  RVBinder<T, VB>? = null,
) : Item<T, VB>() {
    override fun bind(binding: VB, bindingPos: Int) {
        binder?.invoke(BindPayload(this, binding, bindingPos))
    }
}

/**
 * An item that has a single [data] value - a String resource. Good for usage with [textResOrString] in your layout.
 * Set the id in the viewmodel, and do not worry about contexts and other stuff.
 */
data class ResHeaderItem(
    @StringRes override val data: Int,
    override val layout: Int
) : Item<Int, ViewDataBinding>() {
    override val id: Long
        get() = data.toLong()
}

/**
 * An item that has a single [data] value - a String. Good for usage with [textResOrString] in your layout
 */
data class StringHeaderItem(
    override val data: String,
    override val layout: Int,
) : Item<String, ViewDataBinding>() {
    override val id: Long = NO_ID
}

/**
 * You get this payload if you're using [RVBinder].
 * Here's the data you might need when binding.
 */
data class BindPayload<T, VB : ViewDataBinding>(
    val item: Item<T, VB>,
    val binding: VB,
    val bindingPos: Int,
) {
    val data: T get() = item.data
}
