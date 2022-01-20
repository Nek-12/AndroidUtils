@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NewApi")

package com.nek12.androidutils.extensions.core

import java.time.DayOfWeek
import java.time.Instant
import java.time.Month
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*


val ZonedDateTime.midnight: ZonedDateTime get() = truncatedTo(ChronoUnit.DAYS)

fun ZonedDateTime.onSameLocalDay(other: ZonedDateTime): Boolean {
    return truncatedTo(ChronoUnit.DAYS) == other.truncatedTo(ChronoUnit.DAYS)
}

fun Instant.onSameUTCDay(other: Instant): Boolean {
    return truncatedTo(ChronoUnit.DAYS) == other.truncatedTo(ChronoUnit.DAYS)
}

val ZonedDateTime.isToday: Boolean get() = onSameLocalDay(ZonedDateTime.now())

val ZonedDateTime.weekStart: ZonedDateTime
get() = with(ChronoField.DAY_OF_WEEK, 1)

val Instant.localMonthDay get() = toZDT().dayOfMonth

fun Instant.plusDays(offset: Long): Instant {
    return plus(offset, ChronoUnit.DAYS)
}

fun Instant.minusDays(offset: Long): Instant {
    return minus(offset, ChronoUnit.DAYS)
}

fun Instant.asString(formatter: DateTimeFormatter): String {
    return formatter.format(this)
}

/**
 * @return ISO8601 -> Monday = 1
 */
val ZonedDateTime.localWeekDay: Int get() = dayOfWeek.value

fun DayOfWeek.asString(): String = getDisplayName(TextStyle.FULL, Locale.getDefault())

fun Month.asString(): String {
    return getDisplayName(TextStyle.FULL, Locale.getDefault())
}

fun Calendar.setToMidnight() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
