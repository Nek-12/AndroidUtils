@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.nek12.androidutils.databinding.recyclerview.Item.Companion.itemFromData

class ItemBuilder {
    private var currentList = mutableListOf<Item<*, *>>()

    fun addAll(list: Collection<Item<*, *>>): ItemBuilder {
        currentList.addAll(list)
        return this
    }

    fun add(item: Item<*, *>): ItemBuilder {
        currentList.add(item)
        return this
    }

    fun <T> addAll(list: Collection<T>, mapper: (T) -> Item<*, *>) {
        addAll(list.map(mapper))
    }

    fun <T> add(item: T, mapper: (T) -> Item<*, *>) {
        add(mapper(item))
    }

    fun <T, VB : ViewDataBinding> addData(
        data: T,
        @LayoutRes layout: Int,
        id: Long,
        binder: RVBinder<T, VB>?
    ): ItemBuilder {
        currentList.add(itemFromData(data, id, layout, binder))
        return this
    }

    fun <T, VB : ViewDataBinding> addData(
        data: Collection<T>,
        @LayoutRes layout: Int,
        idSelector: (T) -> Long?,
        binder: RVBinder<T, VB>?
    ): ItemBuilder {
        currentList.addAll(data.map { itemFromData(it, idSelector(it), layout, binder) })
        return this
    }

    fun addBlank(@LayoutRes layout: Int): ItemBuilder {
        currentList.add(BlankItem(layout))
        return this
    }

    fun addBlank(@LayoutRes layout: Int, amount: Int): ItemBuilder {
        currentList.addAll((1..amount).map { BlankItem(layout) });
        return this
    }

    fun build(): List<Item<*, *>> = currentList

    fun submit(adapter: GenericAdapter) {
        adapter.submitList(currentList)
    }

    fun <T, VB : ViewDataBinding> addIf(items: Collection<Item<T, VB>>, predicate: (Item<T, VB>) -> Boolean) {
        addAll(items.filter(predicate))
    }

    fun <T, VB : ViewDataBinding> addIf(item: Item<T, VB>, predicate: (Item<T, VB>) -> Boolean) {
        if (predicate(item)) add(item)
    }
}
