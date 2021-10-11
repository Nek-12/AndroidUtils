@file:Suppress("unused")

package com.nek12.androidutils.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

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
        if (actions is RecyclerViewActions.DragActions)
            actions.onItemMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null && actions is RecyclerViewActions.DragActions) {
            actions.onItemSelected(viewHolder)
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (actions is RecyclerViewActions.DragActions)
            actions.onItemReleased(viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (actions is RecyclerViewActions.SwipeActions)
            actions.onItemSwiped(viewHolder, direction)
    }

    override fun isLongPressDragEnabled() = dragEnabled
    override fun isItemViewSwipeEnabled() = swipeEnabled
}
