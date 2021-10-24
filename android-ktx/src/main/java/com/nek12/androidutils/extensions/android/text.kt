@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.graphics.Color
import android.text.*
import android.text.style.*
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import com.nek12.androidutils.extensions.core.isValid
import java.util.*


val String?.isValidEmail: Boolean
    get() = this.isValid && Patterns.EMAIL_ADDRESS.matcher(this!!).matches()

/**
 * Create a span with a [clickablePart] of the text, and invokes the [onClickListener] on click.
 */
fun SpannableString.withClickableSpan(
    clickablePart: String,
    onClickListener: () -> Unit
): SpannableString {
    val clickableSpan = object : ClickableSpan() {
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
 * [span] is a ..Span object like a [ForegroundColorSpan] or a [SuperscriptSpan]
 * Spans this whole string
 */
fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

/**
 * [span] is a ..Span object like a [ForegroundColorSpan] or a [SuperscriptSpan]
 * Spans this whole string
 */
fun String.spanText(span: Any): SpannableStringBuilder = this.toSpannable().spanText(span)


private fun String.toSpannable() = SpannableStringBuilder(this)

/**
 * @return a span where this text has a specified foreground color
 */
fun String.foregroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = ForegroundColorSpan(color)
    return toSpannable().spanText(span)
}

/**
 * @return a span where this string has specified background color
 */
fun String.backgroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = BackgroundColorSpan(color)
    return toSpannable().spanText(span)
}

/**
 * @return a span where this string has specified relative size
 */
fun String.relativeSize(size: Float): SpannableStringBuilder {
    val span = RelativeSizeSpan(size)
    return toSpannable().spanText(span)
}

/**
 * @return a span with this string as a superscript
 */
fun String.superscript(): SpannableStringBuilder {
    val span = SuperscriptSpan()
    return toSpannable().spanText(span)
}

/**
 * @return a span with this text striked through
 */
fun String.strike(): SpannableStringBuilder {
    val span = StrikethroughSpan()
    return toSpannable().spanText(span)
}

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

/**
 * A class that invokes [onChanged] **after** the text changes. Also validates the query.
 * @see TextWatcher
 */
class TextChangeListener(private val onChanged: (newText: String?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        onChanged(s?.toString().takeIf { it.isValid })
    }
}
