package com.nek12.androidutils.databinding.recyclerview

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("textResOrString")
fun textResOrString(view: TextView, data: Any) {
    view.text = when (data) {
        is String -> data
        is Int -> view.context.getString(data)
        else -> throw IllegalArgumentException("You must only supply text resource id or a text string")
    }
}
