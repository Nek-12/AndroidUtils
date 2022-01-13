package com.nek12.androidutils.extensions.android

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <T> Fragment.observe(data: LiveData<T>, block: (value: T?) -> Unit) {
    data.observe(viewLifecycleOwner, block)
}

fun <T> Fragment.observeNotNull(data: LiveData<T>, observer: (value: T) -> Unit) {
    data.observe(viewLifecycleOwner) {
        if (it != null) observer(it)
    }
}

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (value: T) -> Unit) = observe(owner) {
    if (it != null) observer(it)
}

fun <T, R> LiveData<T>.map(mapper: (T) -> R): LiveData<R> = Transformations.map(this, mapper)
