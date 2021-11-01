@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


typealias Inflater<T> = (inflater: LayoutInflater, container: ViewGroup?) -> T

const val VB_NOT_AVAILABLE_MESSAGE = "ViewBinding is not available outside view lifecycle"

abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {
    abstract val inflater: Inflater<T>
    abstract fun onViewReady()

    private var _b: T? = null
    protected val b = requireNotNull(_b) { VB_NOT_AVAILABLE_MESSAGE }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _b = inflater(inflater, container)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
