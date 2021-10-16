@file:Suppress("unused")

package com.nek12.androidutils.extensions

import android.graphics.Color
import android.text.*
import android.text.style.*
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

fun String?.isValid(): Boolean =
    this != null && this.isNotBlank() && !this.equals("null", true)

fun String?.isValidEmail(): Boolean = this.isValid() && Patterns.EMAIL_ADDRESS.matcher(this!!).matches()

fun SpannableString.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): SpannableString {
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
    if (!text?.toString().isValid() || !substring.isValid()) return
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

fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

private fun String.toSpannable() = SpannableStringBuilder(this)

fun String.foregroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = ForegroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.backgroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = BackgroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.relativeSize(size: Float): SpannableStringBuilder {
    val span = RelativeSizeSpan(size)
    return toSpannable().spanText(span)
}

fun String.superscript(): SpannableStringBuilder {
    val span = SuperscriptSpan()
    return toSpannable().spanText(span)
}

fun String.strike(): SpannableStringBuilder {
    val span = StrikethroughSpan()
    return toSpannable().spanText(span)
}

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

fun Int.colorToHexString() = String.format("#%06X", -0x1 and this).replace("#FF", "#")


/**
 * A class that invokes [onChanged] **after** the text changes. Also validates the query
 */
class TextChangeListener(private val onChanged: (newText: String?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        onChanged(s?.toString().takeIf { it.isValid() })
    }
}
