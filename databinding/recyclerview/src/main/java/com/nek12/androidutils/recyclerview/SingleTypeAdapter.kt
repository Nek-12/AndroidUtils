@file:Suppress("unused")

package com.nek12.androidutils.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

open class SingleTypeAdapter<T, VB : ViewDataBinding>(
    @LayoutRes private val layout: Int,
    itemClickListener: ItemClickListener<T>? = null,
    private val binder: Binder<T, VB>? = null,
) : GenericAdapter<T>(itemClickListener) {

    fun submitData(data: List<T>, idSelector: (T) -> Long) {
        submitList(data.map { itemFromData(it, idSelector(it)) })
    }

    fun itemFromData(item: T, id: Long): Item<T, VB> = GenericItem(item, id, layout, binder)
}

open class SimpleAdapter<T>(
    @LayoutRes layout: Int,
    itemClickListener: ItemClickListener<T>? = null,
) : SingleTypeAdapter<T, ViewDataBinding>(layout, itemClickListener, null)
