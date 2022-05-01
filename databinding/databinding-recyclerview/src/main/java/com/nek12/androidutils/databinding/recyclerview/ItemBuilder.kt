@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import com.nek12.androidutils.databinding.recyclerview.Item.Companion.itemFromData

class ItemBuilder {

    private var currentList = mutableListOf<Item<*, *>>()

    fun items(list: Collection<Item<*, *>>): ItemBuilder {
        currentList.addAll(list)
        return this
    }

    fun item(item: Item<*, *>): ItemBuilder {
        currentList.add(item)

        return this
    }

    fun <T> items(list: Collection<T>, mapper: (T) -> Item<*, *>): ItemBuilder = items(list.map(mapper))

    fun <T> item(item: T, mapper: (T) -> Item<*, *>): ItemBuilder = item(mapper(item))

    fun <T, VB: ViewDataBinding> data(
        data: T,
        @LayoutRes layout: Int,
        id: Long,
        binder: RVBinder<T, VB>?
    ): ItemBuilder {
        currentList.add(itemFromData(data, id, layout, binder))
        return this
    }

    fun <T, VB: ViewDataBinding> data(
        data: Collection<T>,
        @LayoutRes layout: Int,
        idSelector: (T) -> Long?,
        binder: RVBinder<T, VB>?
    ): ItemBuilder {
        currentList.addAll(data.map { itemFromData(it, idSelector(it), layout, binder) })
        return this
    }

    fun blank(@LayoutRes layout: Int): ItemBuilder {
        currentList.add(BlankItem(layout))
        return this
    }

    fun blank(@LayoutRes layout: Int, amount: Int): ItemBuilder {
        currentList.addAll((1..amount).map { BlankItem(layout) })
        return this
    }

    fun build(): List<Item<*, *>> = currentList

    fun submit(adapter: GenericAdapter) {
        adapter.submitList(currentList)
    }

    /**
     * Add only items satisfying given [predicate]
     */
    fun <T, VB: ViewDataBinding> filtered(items: Collection<Item<T, VB>>, predicate: (Item<T, VB>) -> Boolean): ItemBuilder {
        return items(items.filter(predicate))
    }

    /**
     * Add an item if it satisfies [predicate]
     */
    fun <T, VB: ViewDataBinding> filtered(item: Item<T, VB>, predicate: (Item<T, VB>) -> Boolean): ItemBuilder {
        if (predicate(item)) item(item)
        return this
    }

    /**
     * The [Item.data] field will have [title] type ([Int])
     * @see ResHeaderItem
     */
    fun header(@StringRes title: Int, @LayoutRes layout: Int): ItemBuilder = item(ResHeaderItem(title, layout))

    /**
     * The [Item.data] field will have [title] type ([String])
     * @see StringHeaderItem
     */
    fun header(title: String, @LayoutRes layout: Int): ItemBuilder = item(StringHeaderItem(title, layout))

    companion object {

        operator fun invoke(block: ItemBuilder.() -> Unit): List<Item<*, *>> {
            val builder = ItemBuilder()
            block(builder)
            return builder.build()
        }
    }
}
