@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.extensions.view

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Let your activity / fragment / etc. implement the actions you want and handle
 * Whatever happens after they happen. You can use it with your recyclerviews to quickly get
 * Dragging, reordering, and swiping.
 */
sealed interface RecyclerViewActions {

    interface SwipeActions : RecyclerViewActions {

        fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    }

    interface DragActions : RecyclerViewActions {

        fun onItemSelected(viewHolder: RecyclerView.ViewHolder)
        fun onItemReleased(viewHolder: RecyclerView.ViewHolder)
        fun onItemMoved(fromPosition: Int, toPosition: Int)
    }
}

/**
 * Use this callback together with [RecyclerViewActions] to get swiping, dragging and reordering.
 *
 * You can set [swipeEnabled] and [dragEnabled] values at runtime to disable and enable
 * functionality as you wish.
 * Example:
 * ```
 * class MyFragment: Fragment(), RecyclerViewActions.DragActions {
 *     // ...
 *
 *     //inside onViewCreated:
 *      ItemTouchHelper(DragCallback(this)).attachToRecyclerView(binding.recyclerView)
 * }
 * ```
 *
 * @param swipeEnabled set this value to enable / disable swiping. By default inferred from the
 * interface implemented in [actions]
 * @param dragEnabled set this value to enable/disable dragging. By default inferred from [actions]
 *
 */
open class DragCallback(
    private val actions: RecyclerViewActions,
    var swipeEnabled: Boolean = actions is RecyclerViewActions.SwipeActions,
    var dragEnabled: Boolean = actions is RecyclerViewActions.DragActions
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        if (actions is RecyclerViewActions.DragActions) {
            actions.onItemMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        }
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE &&
            viewHolder != null &&
            actions is RecyclerViewActions.DragActions
        ) {
            actions.onItemSelected(viewHolder)
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (actions is RecyclerViewActions.DragActions) {
            actions.onItemReleased(viewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (actions is RecyclerViewActions.SwipeActions) {
            actions.onItemSwiped(viewHolder, direction)
        }
    }

    override fun isLongPressDragEnabled() = dragEnabled
    override fun isItemViewSwipeEnabled() = swipeEnabled
}
