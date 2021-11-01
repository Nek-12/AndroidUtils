package com.nek12.androidutils.databinding.recyclerview

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter

/**
 * This is an implementation of a [GenericAdapter] for pagination library.
 * @see Item
 * @see GenericAdapter
 * @see BaseHolder
 */
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

@Suppress("UNCHECKED_CAST")
suspend fun <T, VB : ViewDataBinding> GenericPagingAdapter.submitData(data: PagingData<Item<T, VB>>) {
    submitData(data as PagingData<Item<*, *>>)
}

@Suppress("UNCHECKED_CAST")
fun <T, VB : ViewDataBinding> GenericPagingAdapter.submitData(
    lifecycle: Lifecycle,
    data: PagingData<Item<T, VB>>
) {
    submitData(lifecycle, data as PagingData<Item<*, *>>)
}
