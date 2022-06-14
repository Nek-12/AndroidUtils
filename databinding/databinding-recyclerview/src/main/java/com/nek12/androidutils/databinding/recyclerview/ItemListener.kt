package com.nek12.androidutils.databinding.recyclerview

import android.view.View
import androidx.annotation.LayoutRes

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
sealed interface ItemListener<in T : Item<*, *>>

interface ItemClickListener<in T : Item<*, *>> : ItemListener<T> {

    fun onItemClicked(view: View, item: T, pos: Int)
}

interface ItemLongClickListener<in T : Item<*, *>> : ItemListener<T> {

    fun onItemLongClicked(view: View, item: T, pos: Int): Boolean
}

interface ItemInflateListener<in T : Item<*, *>> : ItemListener<T> {

    fun onViewHolderCreated(holder: BaseHolder, @LayoutRes layout: Int)
}
