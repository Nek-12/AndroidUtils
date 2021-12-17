@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.annotation.AnimatorRes
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.extensions.R
import kotlin.math.floor

const val DEF_FADE_DURATION = 250L
const val IMAGEVIEW_FADE_DURATION = 1000L

/**
 * Whether the device is in landscape mode right now
 */
val Fragment.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * Hides this view, optionally animating it. Default animator fades the view out
 * @param gone should the view be gone **after** the animation ends?
 * @param duration the animation duration
 * @param animation a resource id that can be used to animate the view. Default is fade out
 *
 * @see show
 */
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

/**
 * Shows this view, optionally animating it. Analogous to [hide]
 * @see hide
 */
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

/**
 * A better API for legacy View visibility constants.
 * You can parse a legacy value or a boolean value.
 * [value] is a legacy value like [View.GONE]
 */
enum class Visibility(val value: Int) {
    VISIBLE(View.VISIBLE), HIDDEN(View.INVISIBLE), GONE(View.GONE);

    companion object {
        private val map = values().associateBy(Visibility::value)

        fun of(legacy: Int) = map[legacy]

        /**
         * if [isVisible] is false returns [GONE].
         * Otherwise returns [VISIBLE]
         */
        fun of(isVisible: Boolean) = if (isVisible) VISIBLE else GONE
    }
}
/**
 * Like [View.getVisibility] but uses modern [Visibility] API.
 * @see [Visibility]
 */
var View.currentVisibility: Visibility
    get() = Visibility.of(this.visibility)
        ?: throw IllegalArgumentException("No such visibility value")
    set(value) {
        this.visibility = value.value
    }

/**
 * Calls either [show] or [hide] depending on the [visibility] parameter
 */
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

/**
 * Invisible defaults to [Visibility.GONE]. If you need to override, use [View.setVisibility(visibility: Visibility)]
 */
fun View.setVisibility(
    visible: Boolean,
    animated: Boolean = false,
    duration: Long = DEF_FADE_DURATION
) = setVisibility(Visibility.of(visible), animated, duration)

/**
 * Sets this recyclerview's layout manager to a grid layout manager where the columns are evenly
 * distributed to fill the screen. If you specify 50dp as column width and your screen is
 * 300dp-wide, for example, you will get 6 columns.
 */
fun RecyclerView.autoFitColumns(columnWidthDP: Int, columnSpacingDp: Int) {
    val displayMetrics = this.resources.displayMetrics
    val noOfColumns =
        floor((displayMetrics.widthPixels / displayMetrics.density) / (columnWidthDP.toDouble() + columnSpacingDp.toDouble())).toInt()
    this.layoutManager = GridLayoutManager(this.context, noOfColumns)
}

/**
 * Execute the specified [action] for each viewholder that is currently visible.
 */
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

/**
 * Animates this view as fading in. The visibility is not changed, only the alpha value.
 * @see show
 * @see hide
 * @see setVisibility
 */
fun View.fadeIn(fadeInDuration: Long = IMAGEVIEW_FADE_DURATION) {
    (AnimatorInflater.loadAnimator(this.context, R.animator.fade_in) as AnimatorSet).apply {
        if (isRunning) return
        setTarget(this)
        this.duration = fadeInDuration
        start()
    }
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Fragment.screenWidthPx get() = requireActivity().resources.displayMetrics.widthPixels
val Fragment.screenHeightPx get() = requireActivity().resources.displayMetrics.heightPixels


inline fun <reified T : View> T.onClick(crossinline block: (T) -> Unit) = setOnClickListener { block(it as T) }

fun ScrollView.scrollToView(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        scrollToDescendant(view)
    } else {
        scrollTo(view.scrollX, view.scrollY)
    }
}
