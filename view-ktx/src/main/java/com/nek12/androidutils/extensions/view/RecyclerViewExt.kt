package com.nek12.androidutils.extensions.view

import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.floor

fun ViewGroup.clearRecyclerViewAdapters() {
    children.forEach {
        when (it) {
            is RecyclerView -> it.adapter = null
            is ViewGroup -> it.clearRecyclerViewAdapters()
        }
    }
}

/**
 * Execute the specified [action] for each viewholder that is currently visible.
 */
inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit,
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

fun <T, R : RecyclerView.ViewHolder?> ListAdapter<T, R>.clear() = submitList(emptyList())

/**
 * Sets this recyclerview's layout manager to a grid layout manager where the columns are evenly
 * distributed to fill the screen. If you specify 50dp as column width and your screen is
 * 300dp-wide, for example, you will get 6 columns.
 */
fun RecyclerView.autoFitColumns(columnWidthDP: Int, columnSpacingDp: Int) {
    val displayMetrics = this.resources.displayMetrics
    val noOfColumns = floor(
        displayMetrics.widthPixels / displayMetrics.density / (columnWidthDP.toDouble() + columnSpacingDp.toDouble())
    ).toInt()
    layoutManager = GridLayoutManager(this.context, noOfColumns)
}
