@file:Suppress("unused")

package com.nek12.androidutils.extensions

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimatorRes
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val DEF_FADE_DURATION = 250L
const val IMAGEVIEW_FADE_DURATION = 1000L

val Fragment.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun View.hide(
    animated: Boolean = false,
    gone: Boolean = false,
    duration: Long = DEF_FADE_DURATION,
    @AnimatorRes animation: Int = R.animator.fade_out,
) {
    val visibility = if (gone) View.GONE else View.INVISIBLE
    if (this.visibility == visibility) return
    if (animated) {
        (AnimatorInflater.loadAnimator(context, animation) as AnimatorSet).apply {
            if (isRunning) cancel()
            setTarget(this@hide)
            setDuration(duration)
            doOnEnd { this@hide.visibility = visibility }
            start()
        }
    } else {
        this.visibility = visibility
    }
}

fun View.show(
    animated: Boolean = false,
    duration: Long = DEF_FADE_DURATION,
    @AnimatorRes animation: Int = R.animator.fade_in,
) {
    this@show.visibility = View.VISIBLE
    if (animated) {
        (AnimatorInflater.loadAnimator(context, animation) as AnimatorSet).apply {
            if (isRunning) cancel()
            setTarget(this@show)
            setDuration(duration)
            start()
        }
    }
}

enum class Visibility(val value: Int) {
    VISIBLE(View.VISIBLE), HIDDEN(View.INVISIBLE), GONE(View.GONE);

    companion object {
        private val map = values().associateBy(Visibility::value)

        fun of(legacy: Int) = map[legacy]
        fun of(isVisible: Boolean) = if (isVisible) VISIBLE else GONE
    }
}

var View.currentVisibility: Visibility
    get() = Visibility.of(this.visibility)
        ?: throw IllegalArgumentException("No such visibility value")
    set(value) {
        this.visibility = value.value
    }

fun View.setVisibility(
    visibility: Visibility,
    animated: Boolean = false,
    duration: Long = DEF_FADE_DURATION,
) {
    when (visibility) {
        Visibility.GONE -> hide(animated, true, duration)
        Visibility.HIDDEN -> hide(animated, false, duration)
        Visibility.VISIBLE -> show(animated, duration)
    }
}


fun RecyclerView.autoFitColumns(columnWidthDP: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val noOfColumns =
        ((displayMetrics.widthPixels / displayMetrics.density) / columnWidthDP + 0.5).toInt()
    this.layoutManager = GridLayoutManager(this.context, noOfColumns)
}


inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit,
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}


fun View.fadeIn(fadeInDuration: Long = IMAGEVIEW_FADE_DURATION) {
    (AnimatorInflater.loadAnimator(this.context, R.animator.fade_in) as AnimatorSet).apply {
        if (isRunning) return
        setTarget(this)
        this.duration = fadeInDuration
        start()
    }
}
