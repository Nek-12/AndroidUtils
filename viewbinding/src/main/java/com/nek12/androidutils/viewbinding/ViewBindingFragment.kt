@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.nek12.androidutils.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflater<T> = (inflater: LayoutInflater, container: ViewGroup?, attach: Boolean) -> T

const val VB_NOT_AVAILABLE_MESSAGE = "ViewBinding is not available outside view lifecycle"

@Deprecated("Base classes should not be used. Use composition or delegation instead")
abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {

    abstract val inflater: Inflater<T>
    abstract fun T.onViewReady()

    private var _b: T? = null
    protected val b = requireNotNull(_b) { VB_NOT_AVAILABLE_MESSAGE }

    protected val isViewBound
        get() = _b != null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _b = inflater(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.onViewReady()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
