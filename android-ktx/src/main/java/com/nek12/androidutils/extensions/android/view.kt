@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.annotation.AnimatorRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.extensions.R
import kotlin.math.floor

const val DEF_FADE_DURATION = 250L
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
    val targetVisibility = if (gone) View.GONE else View.INVISIBLE
    if (this.visibility == targetVisibility) return
    if (animated) {
        animate(animation, duration) { this.visibility = targetVisibility }
    } else {
        this.visibility = targetVisibility
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
    if (isVisible) return
    visibility = View.VISIBLE
    if (animated) {
        animate(animation, duration)
    }
}

fun View.fadeIn(duration: Long = DEF_FADE_DURATION) = show(true, duration)

fun View.fadeOut(duration: Long = DEF_FADE_DURATION) = hide(animated = true, gone = true, duration = duration)

/**
 * A better API for legacy View visibility constants.
 * You can parse a legacy value or a boolean value.
 * [value] is a legacy value like [View.GONE]
 */
enum class Visibility(val value: Int) {
    VISIBLE(View.VISIBLE), INVISIBLE(View.INVISIBLE), GONE(View.GONE);

    companion object {
        private val map = values().associateBy(Visibility::value)

        fun of(legacy: Int) = map[legacy]

        /**
         * if [isVisible] is false returns [GONE].
         * Otherwise returns [VISIBLE]
         */
        fun of(isVisible: Boolean, gone: Boolean = true) = when {
            isVisible -> VISIBLE
            gone -> GONE
            else -> INVISIBLE
        }
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
        visibility = value.value
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
        Visibility.INVISIBLE -> hide(animated, false, duration)
        Visibility.VISIBLE -> show(animated, duration)
    }
}

/**
 * Invisible defaults to [Visibility.GONE]. If you need to override, use [View.setVisibility(visibility: Visibility)]
 */
fun View.setVisibility(
    visible: Boolean,
    gone: Boolean = true,
    animated: Boolean = false,
    duration: Long = DEF_FADE_DURATION
) = setVisibility(Visibility.of(visible, gone), animated, duration)

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

fun View.hideKeyboard(): Boolean {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(): Boolean {
    if (requestFocus()) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.showSoftInput(this, 0)
    }
    return false
}

/**
 * Animates this view as fading in. The visibility is not changed, only the alpha value.
 * @see show
 * @see hide
 * @see setVisibility
 */
fun View.animate(
    @AnimatorRes animator: Int,
    duration: Long = DEF_FADE_DURATION,
    onEnd: ((Animator) -> Unit)? = null
) {
    (AnimatorInflater.loadAnimator(context, animator)).apply {
        setTarget(this@animate) //Target view
        this@apply.duration = duration
        onEnd?.let { doOnEnd(it) }
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

fun Context.getDrawableCompat(@DrawableRes id: Int) = AppCompatResources.getDrawable(this, id)!!

/*
 * Credits: https://github.com/tunjid/Android-Extensions/blob/develop/view/src/main/java/com/tunjid/androidx/view/util/ViewUtil.kt
 */
fun ViewGroup.inflate(@LayoutRes res: Int): View =
    LayoutInflater.from(context).inflate(res, this, false)

@get:ColorInt
var View.backgroundTint: Int?
    get() = backgroundTintList?.defaultColor
    set(value) {
        backgroundTintList = value?.let { ColorStateList.valueOf(it) }
    }

fun EditText.setTextPreservingSelection(newText: String?) {
    if (text?.toString() != newText) {
        //setText removes position, restore it to not create jump for the user
        val selection = selectionStart..selectionEnd
        setText(newText)
        setSelection(selection.first, selection.last)
    }
}
