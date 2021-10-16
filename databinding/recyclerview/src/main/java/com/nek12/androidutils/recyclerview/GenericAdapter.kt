@file:Suppress("unused")
package com.nek12.androidutils.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.ListAdapter

/**
 * when user clicks on a view inside the view, determine what part of the itemView was clicked by calling View.id
 */
interface ItemClickListener<T> {
    fun onItemClicked(view: View, item: Item<T, *>, pos: Int)
    fun onItemLongClicked(view: View, item: Item<T, *>, pos: Int): Boolean = false
}

abstract class GenericAdapter<T>(
    private val clickListener: ItemClickListener<T>? = null,
) : ListAdapter<Item<T, *>, BaseHolder>(ItemDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = currentList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val vh = BaseHolder.inflate(parent, viewType)
        return applyListenerToAllViews(vh, clickListener) {
            getItem(vh.bindingAdapterPosition)
        }
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = currentList[position].layout
}

private fun setClickListenersOnViewGroup(view: View, onClick: (v: View) -> Unit) {
    view.setOnClickListener(onClick)
    val group = view as? ViewGroup ?: return
    for (child in group.children) {
        if (child is ViewGroup)
            setClickListenersOnViewGroup(child, onClick)
        else
            child.setOnClickListener(onClick)
    }
}

private fun setLongClickListenersOnViewGroup(view: View, onClick: (v: View) -> Boolean) {
    view.setOnLongClickListener(onClick)
    val group = view as? ViewGroup ?: return
    for (child in group.children) {
        if (child is ViewGroup)
            setLongClickListenersOnViewGroup(child, onClick)
        else
            child.setOnLongClickListener(onClick)
    }
}

fun <T> applyListenerToAllViews(
    vh: BaseHolder,
    clickListener: ItemClickListener<T>?,
    itemSelector: () -> Item<T, *>?
): BaseHolder {
    clickListener?.let {
        setClickListenersOnViewGroup(vh.itemView) {
            clickListener.onItemClicked(
                it,
                itemSelector() ?: return@setClickListenersOnViewGroup,
                vh.bindingAdapterPosition
            )
        }
        setLongClickListenersOnViewGroup(vh.itemView) {
            val item = itemSelector()
            if (item == null) false else {
                clickListener.onItemLongClicked(
                    it,
                    item,
                    vh.bindingAdapterPosition
                )
            }
        }
    }
    return vh
}
