@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.text.HtmlCompat
import com.nek12.androidutils.extensions.core.isValid
import java.util.*

val String?.isValidEmail: Boolean
    get() = isValid && Patterns.EMAIL_ADDRESS.matcher(this!!).matches()

val String?.isValidPhone: Boolean
    get() = isValid && Patterns.PHONE.matcher(this!!).matches()

/**
 * Create a span with a [clickablePart] of the text, and invokes the [onClickListener] on click.
 */
fun SpannableString.withClickableSpan(
    clickablePart: String,
    onClickListener: () -> Unit
): SpannableString {
    val clickableSpan = object: ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
        clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}


/**
 * [span] is a ..Span object like a [ForegroundColorSpan] or a [SuperscriptSpan]
 * Spans this whole string
 */
fun CharSequence.span(span: Any): SpannableString = SpannableString(this).setSpan(span)

private fun CharSequence.buildSpan() = SpannableStringBuilder(this)

private val CharSequence.spannable get() = SpannableString(this)

/**
 * [span] is a ..Span object like a [ForegroundColorSpan] or a [SuperscriptSpan]
 * Spans this whole string
 */
private fun SpannableString.setSpan(span: Any?) = apply {
    setSpan(span, 0, length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
}

fun CharSequence.foregroundColor(@ColorInt color: Int): SpannableString = span(ForegroundColorSpan(color))

fun CharSequence.backgroundColor(@ColorInt color: Int): SpannableString = span(BackgroundColorSpan(color))

fun CharSequence.relativeSize(size: Float): SpannableString = span(RelativeSizeSpan(size))

fun CharSequence.superscript(): SpannableString = span(SuperscriptSpan())

fun CharSequence.subscript() = span(SubscriptSpan())

fun CharSequence.strike(): SpannableString = span(StrikethroughSpan())

fun CharSequence.bold() = span(StyleSpan(Typeface.BOLD))

fun CharSequence.italic() = span(StyleSpan(Typeface.ITALIC))

fun CharSequence.underline() = span(UnderlineSpan())

/**
 * If this is a valid hex color string representation, returns its R, G and B components
 * @throws IllegalArgumentException if the color string is invalid
 *
 */
fun String.hextoRGB(): Triple<Int, Int, Int> {
    var name = this
    if (!name.startsWith("#")) {
        name = "#$this"
    }
    val color = Color.parseColor(name)
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Triple(red, green, blue)
}

/**
 * If this is a color int, turns it into a hex string.
 */
fun Int.colorToHexString() = String.format(Locale.ROOT, "#%06X", -0x1 and this).replace("#FF", "#")

val String.asHTML: Spanned
    get() = HtmlCompat.fromHtml(this, 0)
