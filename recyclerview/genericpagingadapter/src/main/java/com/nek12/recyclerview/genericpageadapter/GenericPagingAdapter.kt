package com.nek12.recyclerview.genericpageadapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.nek12.androidutils.recyclerview.*

open class GenericPagingAdapter<T>(
    private val clickListener: ItemClickListener<T>? = null,
) : PagingDataAdapter<Item<T, *>, BaseHolder>(ItemDiffCallback()) {

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
