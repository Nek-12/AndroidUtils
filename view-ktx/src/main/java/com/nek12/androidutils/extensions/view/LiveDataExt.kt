package com.nek12.androidutils.extensions.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (value: T) -> Unit) = observe(owner) {
    if (it != null) observer(it)
}

/**
 * Notify livedata observers without changing its value
 */
fun <T> MutableLiveData<T>.notify() {
    postValue(value)
}
