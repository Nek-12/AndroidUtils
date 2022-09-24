@file:Suppress("Filename")

package com.nek12.androidutils.databinding.recyclerview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

private const val ILLEGAL_TYPE = "Attempted to set items using databinding for adapter that is not GenericAdapter"

@BindingAdapter("items")
fun items(view: RecyclerView, items: List<Item<*, *>>) {
    (view.adapter as? GenericAdapter)
        ?.submitList(items)
        ?: throw IllegalArgumentException(ILLEGAL_TYPE)
}
