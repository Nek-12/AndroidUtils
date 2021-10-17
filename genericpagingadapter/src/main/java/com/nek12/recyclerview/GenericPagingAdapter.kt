package com.nek12.recyclerview

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.nek12.androidutils.databinding.recyclerview.*

open class GenericPagingAdapter(
    private val clickListener: ItemClickListener<Item<*, *>>? = null,
) : PagingDataAdapter<Item<*, *>, BaseHolder>(ItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position)?.layout ?: 0

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val vh = BaseHolder.inflate(parent, viewType)
        return applyListenerToAllViews(vh, clickListener) {
            getItem(vh.bindingAdapterPosition)
        }
    }
}
