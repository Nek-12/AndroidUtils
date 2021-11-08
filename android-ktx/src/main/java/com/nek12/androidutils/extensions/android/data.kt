package com.nek12.androidutils.extensions.android

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T> Fragment.observe(data: LiveData<T>, block: (value: T?) -> Unit) {
    data.observe(viewLifecycleOwner, block)
}

fun <T> Fragment.observeNotNull(data: LiveData<T>, observer: (value: T) -> Unit) {
    data.observe(viewLifecycleOwner) {
        if (it != null) observer(it)
    }
}
