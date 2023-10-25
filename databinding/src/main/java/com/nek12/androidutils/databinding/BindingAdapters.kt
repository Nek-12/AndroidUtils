@file:Suppress("Filename")

package com.nek12.androidutils.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter

@Deprecated(DEPRECATION_WARNING)
@BindingAdapter("textResOrString")
fun textResOrString(view: TextView, data: Any) {
    view.text = when (data) {
        is String -> data
        is Int -> view.context.getString(data)
        else -> throw IllegalArgumentException("You must only supply string resource id or a text string")
    }
}
