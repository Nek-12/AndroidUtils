@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.extensions.R
import com.nek12.androidutils.extensions.core.isValid
import com.nek12.androidutils.extensions.core.takeIfValid
import kotlin.math.floor

const val DEF_FADE_DURATION = 250L

/**
 * Hides this view, optionally animating it. Default animator fades the view out.
 * Be aware that when view visibility is INVISIBLE and you set it to GONE and vice versa,
 * It makes no sense to animate the view, so the animation won't be run.
 * Also, if you set the same visibility twice, it will be ignored.
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
    when {
        visibility == targetVisibility -> {
            /* nothing to do */
        }
        visibility == View.VISIBLE && animated -> animate(animation, duration) { visibility = targetVisibility }
        else -> visibility = targetVisibility
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
 * Calls either [show] or [hide] depending on the [visible] and [gone] parameters
 */
fun View.setVisibility(
    visible: Boolean,
    gone: Boolean = true,
    animated: Boolean = false,
    duration: Long = DEF_FADE_DURATION,
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
 * Animates this view. The visibility is not changed, only the alpha value.
 * @see show
 * @see hide
 * @see setVisibility
 */
fun View.animate(
    @AnimatorRes animator: Int,
    duration: Long = DEF_FADE_DURATION,
    onEnd: ((Animator) -> Unit)? = null,
) {
    (AnimatorInflater.loadAnimator(context, animator)).apply {
        setTarget(this@animate) //Target view
        this@apply.duration = duration
        onEnd?.let { doOnEnd(it) }
        start()
    }
}


/**
 * Interpret this as DP value and convert it to px. You are responsible for calling this on right value
 */
val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

/**
 * Interpret this as px value and convert it to dp. You are responsible for calling this on right value
 */
val Number.toDp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Fragment.screenWidthPx get() = requireActivity().resources.displayMetrics.widthPixels
val Fragment.screenHeightPx get() = requireActivity().resources.displayMetrics.heightPixels

/**
 * @see [android.util.DisplayMetrics.density]
 */
val Fragment.screenDensity get() = requireActivity().resources.displayMetrics.density


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
fun ViewGroup.inflate(@LayoutRes res: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(res, this, attachToRoot)

@get:ColorInt
var View.backgroundTint: Int?
    get() = backgroundTintList?.defaultColor
    set(value) {
        backgroundTintList = value?.let { ColorStateList.valueOf(it) }
    }

/**
 * Show popup menu, using this view as a base
 */
fun View.showPopup(@MenuRes menu: Int, onMenuItemClick: (item: MenuItem) -> Boolean) {
    val popup = PopupMenu(context, this)
    popup.menuInflater.inflate(menu, popup.menu)
    popup.setOnMenuItemClickListener(onMenuItemClick)
    popup.show()
}

inline fun <reified T : View> T.onClickOrHide(
    noinline onClick: ((view: T) -> Unit)?,
    gone: Boolean = true,
    animated: Boolean = false,
) {
    setVisibility(onClick != null, gone = gone, animated = animated)
    onClick?.let { onClick(it) }
}

fun TextView.setTextOrHide(text: String?, gone: Boolean = false, animated: Boolean = false) {
    setTextKeepState(text)
    setVisibility(text.isValid, gone = gone, animated = animated)
}

fun ImageView.setDrawableOrHide(@DrawableRes res: Int?, gone: Boolean = true, animated: Boolean = false) {
    setVisibility(res != null, gone = gone, animated = animated)
    res?.let(::setImageResource)
}

fun ImageView.setDrawableOrHide(drawable: Drawable?, gone: Boolean = true, animated: Boolean = false) {
    setVisibility(drawable != null, gone = gone, animated = animated)
    drawable?.let(::setImageDrawable)
}

/**
 * [EditText.getText] as a [String], if it [isValid]
 */
val EditText.input get() = text?.toString()?.takeIfValid()

/**
 * You can use this to try and filter actions that weren't triggered by a user (e.g. you setting text yourself)
 * However there is no guarantee that the view will not be focused when you set the text
 */
fun EditText.doAfterTextChangedInFocus(action: (String?) -> Unit) = doAfterTextChanged { text ->
    if (isFocused) action(text?.toString())
}
