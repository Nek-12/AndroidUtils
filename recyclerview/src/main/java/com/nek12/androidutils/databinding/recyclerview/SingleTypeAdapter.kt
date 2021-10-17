@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

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

open class SimpleAdapter<T>(
    @LayoutRes layout: Int,
    itemClickListener: ItemClickListener<Item<T, ViewDataBinding>>? = null,
) : SingleTypeAdapter<T, ViewDataBinding>(layout, itemClickListener, null)
