package com.nek12.androidutils.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

fun String.span(spanStyle: SpanStyle) = buildAnnotatedString {
    withStyle(spanStyle) {
        append(this@span)
    }
}

fun String.bold() = span(SpanStyle(fontWeight = FontWeight.Bold))

fun String.italic() = span(SpanStyle(fontStyle = FontStyle.Italic))

fun String.strike() = span(SpanStyle(textDecoration = TextDecoration.LineThrough))

fun String.underline() = span(SpanStyle(textDecoration = TextDecoration.Underline))

fun String.decorate(vararg decorations: TextDecoration) =
    span(SpanStyle(textDecoration = TextDecoration.combine(decorations.toList())))

fun String.size(size: TextUnit) = span(SpanStyle(fontSize = size))

fun String.background(color: Color) = span(SpanStyle(background = color))

fun String.color(color: Color) = span(SpanStyle(color = color))

fun String.weight(weight: FontWeight) = span(SpanStyle(fontWeight = weight))

fun String.style(style: FontStyle) = span(SpanStyle(fontStyle = style))

fun String.shadow(color: Color = Color(0xFF000000), offset: Offset = Offset.Zero, blurRadius: Float = 0.0f) =
    span(SpanStyle(shadow = Shadow(color, offset, blurRadius)))

fun String.fontFamily(fontFamily: FontFamily) = span(SpanStyle(fontFamily = fontFamily))
