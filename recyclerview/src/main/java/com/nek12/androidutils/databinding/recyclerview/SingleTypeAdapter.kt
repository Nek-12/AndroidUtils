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
open class SingleTypeAdapter<T, VB : ViewDataBinding>(
    @LayoutRes private val layout: Int,
    itemClickListener: ItemClickListener<Item<T, VB>>? = null,
    private val binder: Binder<T, VB>? = null,
) : GenericAdapter(itemClickListener as ItemClickListener<Item<*,*>>) {

    fun submitData(data: List<T>, idSelector: (T) -> Long) {
        submitList(data.map { itemFromData(it, idSelector(it)) })
    }

    fun itemFromData(item: T, id: Long): Item<T, VB> = GenericItem(item, id, layout, binder)
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
 */
open class SimpleAdapter<T>(
    @LayoutRes layout: Int,
    itemClickListener: ItemClickListener<Item<T, ViewDataBinding>>? = null,
) : SingleTypeAdapter<T, ViewDataBinding>(layout, itemClickListener, null)
