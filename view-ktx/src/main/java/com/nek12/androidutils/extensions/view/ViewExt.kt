@file:Suppress("unused")

package com.nek12.androidutils.extensions.view

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.AnimatorRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.nek12.androidutils.extensions.android.isValid

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
    @AnimatorRes animation: Int = com.nek12.extensions.android.R.animator.fade_out,
) {
    val targetVisibility = if (gone) View.GONE else View.INVISIBLE
    when {
        visibility == targetVisibility -> {
            /* nothing to do */
        }
        visibility == View.VISIBLE && animated -> animate(animation, duration) {
            visibility =
                targetVisibility
        }
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
    @AnimatorRes animation: Int = com.nek12.extensions.android.R.animator.fade_in,
) {
    if (isVisible) return
    visibility = View.VISIBLE
    if (animated) {
        animate(animation, duration)
    }
}

fun View.fadeIn(duration: Long = DEF_FADE_DURATION) = show(true, duration)

fun View.fadeOut(duration: Long = DEF_FADE_DURATION) = hide(
    animated = true,
    gone = true,
    duration = duration
)

/**
 * A better API for legacy View visibility constants.
 * You can parse a legacy value or a boolean value.
 * [value] is a legacy value like [View.GONE]
 */
enum class Visibility(val value: Int) {

    VISIBLE(View.VISIBLE), INVISIBLE(View.INVISIBLE), GONE(View.GONE);

    companion object {

        private val map = entries.associateBy(Visibility::value)

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
    AnimatorInflater.loadAnimator(context, animator).apply {
        setTarget(this@animate) // Target view
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

inline fun <reified T : View> T.onClick(crossinline block: (T) -> Unit) = setOnClickListener {
    block(
        it as T
    )
}

fun ScrollView.scrollToView(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        scrollToDescendant(view)
    } else {
        scrollTo(view.scrollX, view.scrollY)
    }
}

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

fun ImageView.setDrawableOrHide(
    @DrawableRes res: Int?,
    gone: Boolean = true,
    animated: Boolean = false
) {
    setVisibility(res != null, gone = gone, animated = animated)
    res?.let(::setImageResource)
}

fun ImageView.setDrawableOrHide(
    drawable: Drawable?,
    gone: Boolean = true,
    animated: Boolean = false
) {
    setVisibility(drawable != null, gone = gone, animated = animated)
    drawable?.let(::setImageDrawable)
}

/**
 * [EditText.getText] as a [String], if it [isValid]
 */
val EditText.input get() = text?.toString()?.takeIf { it.isValid }

/**
 * You can use this to try and filter actions that weren't triggered by a user (e.g. you setting text yourself)
 * However there is no guarantee that the view will not be focused when you set the text
 */
fun EditText.doAfterTextChangedInFocus(action: (String?) -> Unit) = doAfterTextChanged { text ->
    if (isFocused) action(text?.toString())
}

/**
 * Set the image drawable for this [ImageView] using [avdResId], then start animating it.
 * The animation runs in loops and never stops.
 */
@SuppressLint("UseCompatLoadingForDrawables")
// Requires M api and does not use AppCompat because the animation won't work when using appcompat drawable, tested
fun ImageView.applyLoopingAVD(@DrawableRes avdResId: Int) {
    val animated = resources.getDrawable(
        avdResId,
        context.theme
    ) as? AnimatedVectorDrawable ?: throw IllegalArgumentException("Invalid drawable")
    applyLoopingAVD(animated)
}

fun ImageView.applyLoopingAVD(avd: AnimatedVectorDrawable) {
    avd.registerAnimationCallback(object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            this@applyLoopingAVD.post { avd.start() }
        }
    })
    this.setImageDrawable(avd)
    avd.start()
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    if (!text?.toString().isValid || !substring.isValid) return
    val spannable = SpannableString(text)
    val start = text.indexOf(substring)
    spannable.setSpan(
        ForegroundColorSpan(color),
        start,
        start + substring.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = spannable
}

/**
 * A class that invokes [onChanged] **after** the text changes. Also validates the query.
 * @see TextWatcher
 */
class TextChangeListener(private val onChanged: (newText: String?) -> Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        /* do nothing */
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        /* do nothing */
    }

    override fun afterTextChanged(s: Editable?) {
        onChanged(s?.toString().takeIf { it.isValid })
    }
}
