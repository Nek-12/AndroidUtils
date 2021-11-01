package com.nek12.androidutils.databinding.recyclerview

import androidx.recyclerview.widget.DiffUtil

/**
 * If any of the items' id is null, returns false, this way the diff will always be recalculated
 * and the item is going to be rebound properly.
 * Compares items using equals()
 * You should not be required to use this class at all.
 */
class ItemDiffCallback<T : Item<*, *>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}
