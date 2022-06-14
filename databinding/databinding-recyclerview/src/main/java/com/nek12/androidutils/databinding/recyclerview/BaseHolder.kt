@file:Suppress("UnusedImport")

package com.nek12.androidutils.databinding.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.databinding.BR

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
class BaseHolder(val binding: ViewDataBinding, private val brVariable: Int = BR.data) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item<*, *>) {
        if (item.data != Unit) {
            binding.setVariable(brVariable, item.data)
        }
        binding.executePendingBindings()
        item.tryBind(binding, bindingAdapterPosition)
    }

    companion object {

        inline fun <reified T : ViewDataBinding> inflate(
            parent: ViewGroup,
            @LayoutRes layout: Int,
            lifecycleOwner: LifecycleOwner? = null,
            brVariable: Int = BR.data
        ): BaseHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: T? = DataBindingUtil.inflate(
                layoutInflater, layout, parent, false
            )
            requireNotNull(binding) { "Couldn't inflate binding, check your layout ID: $layout" }
            binding.lifecycleOwner = lifecycleOwner
            return BaseHolder(binding, brVariable)
        }
    }
}
