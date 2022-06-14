@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

/**
 * A concrete implementation of [GenericAdapter] that is intended for use with lists that have
 * single view type. You don't have to implement items with this adapter, just supply a layout
 * and use [submitData] instead of [submitList].
 * Call [submitData] providing a list of [Item.data] like objects that will be passed
 * on to xml, add optional binding logic if you are not satisfied with xml binding and you're done.
 * Your [Item]s or external class, however, must have some field to replace the [Item.id]
 * For cases when you have multiple view types, it is recommended to use [GenericAdapter] and [Item]
 */
@Suppress("UNCHECKED_CAST")
open class SingleTypeAdapter<T, in VB : ViewDataBinding>(
    @LayoutRes private val layout: Int,
    itemClickListener: ItemListener<Item<T, VB>>? = null,
    lifecycleOwner: LifecycleOwner? = null,
    stableIds: Boolean = true,
    brVariable: Int = BR.data,
    private val binder: RVBinder<T, VB>? = null,
) : GenericAdapter(itemClickListener as? ItemListener<Item<*, *>>?, lifecycleOwner, stableIds, brVariable) {

    override fun getItem(pos: Int): Item<T, VB> {
        return super.getItem(pos) as Item<T, VB>
    }

    fun submitData(data: List<T>, idSelector: (T) -> Long?) = this.submitData(data, layout, idSelector, binder)

    override fun getCurrentList(): List<Item<T, VB>> = super.getCurrentList() as List<Item<T, VB>>
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
    itemClickListener: ItemListener<Item<T, ViewDataBinding>>? = null,
    lifecycleOwner: LifecycleOwner? = null,
    brVariable: Int = BR.data,
    stableIds: Boolean = true,
) : SingleTypeAdapter<T, ViewDataBinding>(layout, itemClickListener, lifecycleOwner, stableIds, brVariable, null)
