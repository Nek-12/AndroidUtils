@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.databinding.DataBindingFragment

abstract class RVDataBindingFragment<T : ViewDataBinding> : DataBindingFragment<T>() {
    abstract fun selectRecyclers(binding: T): List<RecyclerView>
    override fun onDestroyView() {
        selectRecyclers(b).forEach {
            it.adapter = null
        }
        super.onDestroyView()
    }
}
