package com.nek12.androidutils.databinding.recyclerview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("items")
fun items(view: RecyclerView, items: List<Item<*, *>>) {
    (view.adapter as? GenericAdapter)?.submitList(items)
        ?: throw IllegalArgumentException("Attempted to set items using databinding for adapter that is not GenericAdapter")
}
