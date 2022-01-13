package com.nek12.androidutils.extensions.android

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnDetach
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Sets an adapter for the recycler view that will be cleared after [View.onDetachedFromWindow], preventing memory leaks
 * from having [RecyclerView.Adapter] that outlives its recyclerview (e.g. when stored as a field in a Fragment)
 */
var RecyclerView.safeAdapter
    get() = adapter
    set(value) {
        adapter = value
        doOnDetach {
            adapter = null
        }
    }

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
