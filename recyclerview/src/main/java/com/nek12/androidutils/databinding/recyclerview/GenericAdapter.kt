@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.ListAdapter

/**
 * A clicklistener that is fired when the user clicks on **any** of the views inside your Item view.
 * For example, if the user clicks on a button, you will get this button as a [view] parameter
 * and when they click on an empty space around it you will get **root** view like ConstraintLayout.
 * Determine what part of the itemView was clicked by calling View.id.
 * If you struggle to handle [Item] generic type, create a sealed class which extends [Item].
 *
 * Example:
 * ```
 * private val listener = object : ItemClickListener<Item<*,*>> {
 *     override fun onItemClicked(view: View, item: Item<*, *>, pos: Int) {
 *          when (view.id) {
 *              R.id.edit -> editInstance(item)
 *              R.id.start -> requestStartEntryAction(pos)
 *              else -> { /* ignore layouts that do not interest us */ }
 *         }
 *     }
 * }
 * ```
 * @see Item
 * @see GenericAdapter
 */
interface ItemClickListener<in T : Item<*, *>> {
    fun onItemClicked(view: View, item: T, pos: Int)
    fun onItemLongClicked(view: View, item: T, pos: Int): Boolean = false
}

/**
 * The base class for your adapter implementations. In general, you should not be required to
 * extend this class, except for rare customization cases. In most cases you simply create this
 * adapter instance in your `onViewCreated()` and assign it to the recycler. You populate this
 * adapter with your [Item]s which do all the dirty work. The [Item]s should come from the
 * viewModel (or other business logic processing place), and if you need them to be created in the
 * fragment / activity itself, this is a sign that your logic could be better, in most cases.
 * This generic adapter enables all possible optimizations that can be used with RecyclerView:
 * - Item Diffing
 * - Stable Ids
 * - ListAdapter optimizations
 *
 * So you should not worry about them now. Be aware that in some cases, item diffing and stable
 * ids support can degrade based on what you do in your items (e.g. if you provide non-unique
 * [Item.id]s or implement [Item.equals] poorly). Making a good [Item] is your sole responsibility
 * now.
 *
 * @see Item
 * @see ItemClickListener
 * @see SingleTypeAdapter
 * @see SimpleAdapter
 * @see GenericItem
 */
open class GenericAdapter(
    private val clickListener: ItemClickListener<Item<*, *>>? = null,
) : ListAdapter<Item<*, *>, BaseHolder>(ItemDiffCallback()) {

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
        if (child is ViewGroup) {
            setClickListenersOnViewGroup(child, onClick)
        } else {
            child.setOnClickListener(onClick)
        }
    }
}

private fun setLongClickListenersOnViewGroup(view: View, onClick: (v: View) -> Boolean) {
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

fun <T : Item<*, *>> applyListenerToAllViews(
    vh: BaseHolder,
    clickListener: ItemClickListener<T>?,
    itemSelector: () -> T?
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
