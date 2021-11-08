@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * The base class for your adapter implementations. In general, you should not be required to
 * extend this class, except for rare customization cases. In most cases you simply create this
 * adapter instance in your `onViewCreated()` and assign it to the recycler. You populate this
 * adapter with your [Item]s which do all the dirty work. The [Item]s should come from the
 * viewModel (or other business logic processing place), and if you need them to be created in the
 * fragment / activity itself, this is a sign that your logic could be better, in most cases.
 * This generic adapter enables all possible optimizations that can be used with RecyclerView:
 * - Async Item Diffing
 * - Stable Ids
 * - ListAdapter optimizations
 *
 * So you should not worry about them now. Be aware that in some cases, item diffing and stable
 * ids support can degrade based on what you do in your items (e.g. if you set [Item.NO_ID] as an ID
 * or implement [Item.equals] poorly). Making a good [Item] is your sole responsibility now.
 * Be sure to **never** supply a list of items, where [Item.id]s are **not unique**. This will crash your app sooner
 * or later.
 *
 * @see Item
 * @see ItemListener
 * @see SingleTypeAdapter
 * @see SimpleAdapter
 * @see GenericItem
 */
open class GenericAdapter(
    private val listener: ItemListener<Item<*, *>>? = null,
    private val lifecycleOwner: LifecycleOwner? = null,
    private val stableIds: Boolean = true,
) : ListAdapter<Item<*, *>, BaseHolder>(ItemDiffCallback()) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        setHasStableIds(stableIds)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemId(position: Int): Long = currentList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val vh = BaseHolder.inflate<ViewDataBinding>(parent, viewType, lifecycleOwner)
        (listener as? ItemInflateListener)?.onViewHolderCreated(vh, viewType)
        return applyListenerToAllViews(vh, listener) {
            //defers getting item by position using a lambda object
            //when position is available
            getItem(vh.bindingAdapterPosition)
        }
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = currentList[position].layout
}

@PublishedApi
internal fun setClickListenersOnViewGroup(view: View, onClick: (v: View) -> Unit) {
    view.setOnClickListener(onClick)
    val group = view as? ViewGroup ?: return
    for (child in group.children) {
        if (child is ViewGroup) {
            setClickListenersOnViewGroup(child, onClick)
        } else {
            child.setOnClickListener(onClick)
        }
    }
}

@PublishedApi
internal fun setLongClickListenersOnViewGroup(view: View, onClick: (v: View) -> Boolean) {
    view.setOnLongClickListener(onClick)
    val group = view as? ViewGroup ?: return
    for (child in group.children) {
        if (child is ViewGroup) {
            setLongClickListenersOnViewGroup(child, onClick)
        } else {
            child.setOnLongClickListener(onClick)
        }
    }
}

inline fun <T : Item<*, *>> applyListenerToAllViews(
    vh: BaseHolder,
    clickListener: ItemListener<T>?,
    crossinline itemSelector: () -> T?
): BaseHolder {
    clickListener?.let {
        if (clickListener is ItemClickListener<T>) {
            setClickListenersOnViewGroup(vh.itemView) {
                clickListener.onItemClicked(
                    it,
                    itemSelector() ?: return@setClickListenersOnViewGroup,
                    vh.bindingAdapterPosition
                )
            }
        }
        if (clickListener is ItemLongClickListener<T>) {
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
    }
    return vh
}
