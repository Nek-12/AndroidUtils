@file:Suppress("unused")

package com.nek12.androidutils.extensions.core

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.math.abs
import kotlin.math.log10
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
    val list = this.toMutableList()
    return list.swap(index1, index2)
}

@Suppress("NewApi")
fun Calendar.setDayOfWeek(dayOfWeek: DayOfWeek) {
    // mapping 1=mon..7=sun -> 1=sun..7=mon
    val mapped: Int = if (dayOfWeek.value == 7) 1 else dayOfWeek.value + 1
    set(Calendar.DAY_OF_WEEK, mapped)
}

/**
 * The count of digits in this [Int]
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
    operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values)
    {
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
    get() = !this.isNullOrBlank() && !this.equals("null", true)
