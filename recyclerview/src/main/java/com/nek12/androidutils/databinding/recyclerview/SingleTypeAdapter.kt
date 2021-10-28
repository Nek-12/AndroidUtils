@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * A concrete implementation of [GenericAdapter] that is intended for use with lists that have
 * single view type. You don't have to implement items with this adapter, just supply a layout
 * and use [submitData] instead of [submitList].
 * Call [submitData] providing a list of [Item.data] like objects that will be passed
 * on to xml, add optional binding logic if you are not satisfied with xml binding and you're done.
 * Your [data]s or external class, however, must have some field to replace the [Item.id]
 * For cases when you have multiple view types, it is recommended to use [GenericAdapter] and [Item]
 */
@Suppress("UNCHECKED_CAST")
open class SingleTypeAdapter<T, in VB : ViewDataBinding>(
    @LayoutRes private val layout: Int,
    itemClickListener: ItemClickListener<Item<T, VB>>? = null,
    private val binder: RVBinder<T, VB>? = null,
) : GenericAdapter(itemClickListener as? ItemClickListener<Item<*, *>>?) {

    /**
     * Like [submitList], but transforms your data objects into Items for you.
     * **If you don't have anything to serve as an ID, let the idSelector return
     * [Item.NO_ID] or _null_. In this case
     * you will lose some performance, so make sure you
     * resort to this when you truly have no other alternative.
     */
    fun submitData(data: List<T>, idSelector: (T) -> Long?) {
        submitList(data.map { itemFromData(it, idSelector(it)) })
    }

    fun itemFromData(item: T, id: Long?): Item<T, VB> =
        GenericItem(item, id ?: Item.NO_ID, layout, binder)
}

/**
 * This is the simplest possible list implementation. This adapter is intended for use cases when
 * you have a single view type and do not require any custom binding logic (you do everything in
 * your XML or use binding adapters to do the job). When you need custom binding logic, use
 * [SingleTypeAdapter]. When you need multiple view types, use [GenericAdapter].
 * Do **not** use [submitList], use [submitData]
 *
 * example:
 * ```
 * val adapter = SimpleAdapter<MenuEntry>(R.layout.menu_item) //literally one-liner recyclerview
 * adapter.submitData(data = itemsIGotSomewhereElse) { it.id }
 * binding.recyclerView.adapter = adapter
 *
 * ```
 * @see SingleTypeAdapter
 * @see GenericAdapter
 * @see GenericItem
 */
open class SimpleAdapter<T>(
    @LayoutRes layout: Int,
    itemClickListener: ItemClickListener<Item<T, ViewDataBinding>>? = null,
) : SingleTypeAdapter<T, ViewDataBinding>(layout, itemClickListener, null)
