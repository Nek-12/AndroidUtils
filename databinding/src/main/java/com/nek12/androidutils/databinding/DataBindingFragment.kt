package com.nek12.androidutils.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * A BaseFragment class that you're so tired of implementing each darn time in your app.
 * Supply a [layoutRes] and [T] arguments and don't waste your time.
 * You don't need to override [onCreateView] or inflate anything, just use your binding straight
 * away in [onViewCreated] or in other places that are called **during the view lifecycle**.
 * Remember, if you override [onDestroyView], call your code that used binding first, then call
 * super.onDestroyView()
 *
 * @param b your new binding variable
 * @param layoutRes a layout for this fraagment
 */
abstract class DataBindingFragment<T : ViewDataBinding> : Fragment() {
    protected abstract val layoutRes: Int
    private var _b: T? = null
    protected val b: T
        get() = _b
            ?: throw IllegalAccessException("ViewBinding is not available outside of the view lifecycle")

    abstract fun onViewReady()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        b.lifecycleOwner = viewLifecycleOwner
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
