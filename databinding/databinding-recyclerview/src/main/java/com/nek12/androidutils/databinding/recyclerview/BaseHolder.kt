@file:Suppress("UnusedImport")

package com.nek12.androidutils.databinding.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * The generic databinding ViewHolder class that is used internally in the library.
 * This class is created and managed internally. What you're searching for is [Item] probably.
 * You should almost never be required to somehow operate on that class except when you want to
 * get the [binding] field.
 *
 * **Remember that this holder operates on premise that the variable in your layout xml for the
 * item is called exactly "data" and the type is of the first type argument for your [Item] class
 * implementation (i.e. the type of the "data" field).**
 */
class BaseHolder(val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item<*, *>) {
        if (item.data != Unit) {
            binding.setVariable(BR.data, item.data)
        }
        binding.executePendingBindings()
        item.tryBind(binding, bindingAdapterPosition)
    }

    companion object {
        fun inflate(
            parent: ViewGroup,
            @LayoutRes layout: Int,
            lifecycleOwner: LifecycleOwner? = null,
        ): BaseHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, layout, parent, false
            )
            binding.lifecycleOwner = lifecycleOwner
            return BaseHolder(binding)
        }
    }
}
