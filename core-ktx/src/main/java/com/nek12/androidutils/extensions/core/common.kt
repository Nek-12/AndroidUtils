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
import kotlin.reflect.KProperty

/**
 * @param selector is a function using which the value by which we reorder is going to be selected, must be the same
 * value that is specified in the [order]
 * @param order a list of objects that represents the order which should be used to sort the original list
 * @returns A copy of this list ordered according to the order
 */
fun <T, R> Iterable<T>.reorderBy(order: List<R>, selector: (T) -> R): List<T> {
    // associate the values with indexes and create a map
    val orderMap = order.withIndex().associate { it.value to it.index }
    // sort the habits sorted using the comparator that places values not present in a map last
    // and uses the order of the items in the map as its basis for sorting.
    return sortedWith(compareBy(nullsLast()) { orderMap[selector(it)] }).toMutableList()
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int): MutableList<T> {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
    return this
}

/**
 * Returns a shallow copy of this list with the items swapped
 */
fun <T> List<T>.swapped(index1: Int, index2: Int): List<T> {
    val list = toMutableList()
    return list.swap(index1, index2)
}

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
 * This is an experimental implementation of a lazy property delegate that stores its state.
 * Mainly for extending other classes with lazy properties.
 */
class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {

    private val values = WeakHashMap<This, Return>()

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values) {
        thisRef as This
        return values.getOrPut(thisRef) { thisRef.initializer() }
    }
}

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

fun <T> List<T>.randomElements(count: Int): List<T> = shuffled().take(count)

/**
 * Check if this String has length in [range]
 */
infix fun String.spans(range: IntRange) = length in range

fun Float.format(digits: Int) = "%.${digits}f".format(this)

val String.isAscii: Boolean get() = toCharArray().none { it < ' ' || it > '~' }

val String.asUUID: UUID
    get() = UUID.fromString(this)

fun String?.isValidPattern(pattern: Pattern) = isValid && pattern.matcher(this!!).matches()

fun <T> MutableCollection<T>.replaceWith(src: Collection<T>) {
    clear()
    addAll(src)
}

val BigDecimal.sign: String get() = if (signum() < 0) "–" else ""

/**
 * @return a new list, where each item that matches [predicate] is replaced with [with]
 */
inline fun <T> List<T>.replaceIf(with: T, predicate: (T) -> Boolean): List<T> = map { if (predicate(it)) with else it }

/**
 * @param apply a function that is applied on each item that matches [predicate]
 * @return a new list, where each item that matches [predicate] is replaced with the result of applying [apply] to it
 */
inline fun <T> List<T>.replaceIf(apply: T.() -> T, predicate: (T) -> Boolean): List<T> =
    map { if (predicate(it)) apply(it) else it }

val Long.asDate get() = Date(this)

val Int.asDate get() = Date(toLong())

fun <T> Iterable<T>.indexOfFirstOrNull(predicate: (T) -> Boolean) = indexOfFirst(predicate).takeIf { it != -1 }

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

/**
 * Fills the list with **shallow** copies of the receiver
 */
fun <T> T.copies(count: Int): List<T> {
    val list = mutableListOf<T>()
    repeat(count) {
        list.add(this)
    }
    return list
}

/**
 * Creates a copy of [this] list with [item] replaced with whatever is returned by [replacement].
 *
 * [item] **MUST** be an item contained in the original list, or you will get an [IndexOutOfBoundsException]
 */
inline fun <T> List<T>.replace(item: T, replacement: T.() -> T): List<T> {
    val newList = toMutableList()
    newList[indexOf(item)] = replacement(item)
    return newList
}

/**
 * @return null if the collection is empty, else ther result of [single]
 */
fun <T> Iterable<T>.singleOrNullIfEmpty() = if (none()) null else single()


val ClosedRange<Int>.size get() = endInclusive - start

val ClosedRange<Double>.size get() = endInclusive - start

val ClosedRange<Float>.size get() = endInclusive - start

val ClosedRange<Long>.size get() = endInclusive - start

val ClosedRange<Short>.size @JvmName("sizeShort") get() = endInclusive - start

val ClosedRange<Byte>.size @JvmName("sizeByte") get() = endInclusive - start

val Iterable<Time?>.totalDuration: Time
    get() = Time.fromSecondsSinceMidnight(this.sumOf { it?.secondsSinceMidnight?.toLong() ?: 0L })
