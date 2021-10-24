@file:Suppress("unused")

package com.nek12.androidutils.databinding.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.databinding.DataBindingFragment

/**
 * This is a base fragment class for fragments that have 1 or more recyclerviews.
 * Use this instead of [DataBindingFragment] because you must clear your adapters from
 * recyclerviews in onDestroyView. Just supply them in [selectRecyclers]
 */
abstract class RVDataBindingFragment<T : ViewDataBinding> : DataBindingFragment<T>() {
    abstract val recyclerSelector: (binding: T) -> List<RecyclerView>

    override fun onDestroyView() {
        recyclerSelector(b).forEach {
            it.adapter = null
        }
        super.onDestroyView()
    }
}
