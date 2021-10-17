package com.nek12.androidutils.databinding.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView

class BaseHolder(val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item<*, *>) {
        //TODO: May be a slow op
        if (item.data == null || item.data is Unit) return
        binding.setVariable(BR.item, item.data)
        binding.executePendingBindings()
        item.tryBind(binding, bindingAdapterPosition)
    }

    companion object {
        fun inflate(
            parent: ViewGroup,
            @LayoutRes layout: Int
        ): BaseHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, layout, parent, false
            )
            return BaseHolder(binding)
        }
    }
}
