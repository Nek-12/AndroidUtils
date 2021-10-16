package com.nek12.androidutils.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class DataBindingFragment<T : ViewDataBinding> : Fragment() {
    protected abstract val layoutRes: Int
    private var _b: T? = null
    protected val b: T
        get() = _b
            ?: throw IllegalAccessException("ViewBinding is not available outside of the view lifecycle")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return b.root
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
