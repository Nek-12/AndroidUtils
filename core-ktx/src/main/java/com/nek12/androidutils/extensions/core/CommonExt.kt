@file:Suppress("unused")

package com.nek12.androidutils.extensions.core

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.WeekFields
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.log10
import kotlin.math.sign

@Suppress("NewApi")
fun Calendar.setDayOfWeek(dayOfWeek: DayOfWeek) {
    // mapping 1=mon..7=sun -> 1=sun..7=mon
    val mapped: Int = if (dayOfWeek.value == 7) 1 else dayOfWeek.value + 1
    set(Calendar.DAY_OF_WEEK, mapped)
}

/**
 * The number of digits in this [Int]
 */
val Int.length
    get() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }

fun Boolean.toInt(): Int = if (this) 1 else 0

@Suppress("NewApi")
fun Instant.toZDT(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime = ZonedDateTime.ofInstant(this, zoneId)

/**
 * @return Whether this string is valid
 * Examples:
 * - null -> false
 * - "null" -> false
 * - "" -> false
 * - "NULL" -> false
 * - "  " -> false
 */
val String?.isValid: Boolean
    get() = !isNullOrBlank() && !equals("null", true)

fun String?.takeIfValid(): String? = if (isValid) this else null

/**
 * Check if this String has length in [range]
 */
infix fun String.spans(range: IntRange) = length in range

fun Float.format(digits: Int) = "%.${digits}f".format(this)

val String.isAscii: Boolean get() = toCharArray().none { it < ' ' || it > '~' }

val String.asUUID: UUID
    get() = UUID.fromString(this)

fun String?.isValidPattern(pattern: Pattern) = isValid && pattern.matcher(this!!).matches()

val BigDecimal.sign: String get() = if (signum() < 0) "–" else ""

val Long.asDate get() = Date(this)

val Int.asDate get() = Date(toLong())

/**
 * Uses [LazyThreadSafetyMode.NONE] to provide values quicker
 */
fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * Filter this list by searching for elements that contain [substring],
 * or if string is not [String.isValid], the list itself
 * @param substring a string, that must be [String.isValid]
 * @return a resulting list
 */
fun List<String>.filterBySubstring(substring: String?): List<String> {
    return if (substring.isValid) asSequence()
        .filter { it.contains(substring!!, true) }
        .toList()
    else this
}

@Suppress("NewApi")
fun List<DayOfWeek>.sortedByLocale(locale: Locale = Locale.getDefault()): List<DayOfWeek> {
    val first = WeekFields.of(locale).firstDayOfWeek
    val all = this.toMutableList()
    if (all.remove(first))
        all.add(0, first)
    return all
}

/**
 * Returns the sign of the number, as a char
 */
val <T : Number> T.signChar: String
    get() {
        val repr = this.toByte()
        return when {
            repr < 0 -> "-"
            repr > 0 -> "+"
            else -> ""
        }
    }

fun Int.toStringWithSign() = "$sign$absoluteValue"

val ClosedRange<Int>.size get() = endInclusive - start

val ClosedRange<Double>.size get() = endInclusive - start

val ClosedRange<Float>.size get() = endInclusive - start

val ClosedRange<Long>.size get() = endInclusive - start

val ClosedRange<Short>.size @JvmName("sizeShort") get() = endInclusive - start

val ClosedRange<Byte>.size @JvmName("sizeByte") get() = endInclusive - start

val Iterable<Time?>.totalDuration: Time
    get() = Time.fromSecondsSinceMidnight(this.sumOf { it?.totalSeconds?.toLong() ?: 0L })

fun <T : Number> T?.takeIfNotZero() = takeIf { it != 0 }
