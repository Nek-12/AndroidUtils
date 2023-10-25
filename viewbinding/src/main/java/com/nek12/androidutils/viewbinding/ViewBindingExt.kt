package com.nek12.androidutils.viewbinding

import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

fun ViewBinding.getString(@StringRes id: Int) = root.context.getString(id)
fun ViewBinding.getString(@StringRes id: Int?) = id?.let { getString(it) }
fun ViewBinding.getString(@StringRes id: Int, vararg args: Any?) = root.context.getString(id, *args)
fun ViewBinding.getAttribute(@AttrRes attr: Int): Int {
    val value = TypedValue()
    root.context.theme.resolveAttribute(attr, value, true)
    return value.data
}

fun ViewBinding.dimension(@DimenRes dimen: Int) = root.resources.getDimension(dimen).toInt()
fun ViewBinding.drawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(root.context, drawable)!!
