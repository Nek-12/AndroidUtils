package com.nek12.androidutils.databinding.recyclerview

import androidx.recyclerview.widget.DiffUtil

class ItemDiffCallback<T : Item<*, *>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}
