package com.nek12.androidutils.extensions.android

import android.text.format.DateUtils
import java.util.*

val Date.isToday get() = DateUtils.isToday(time)

val Date.isYesterday get() = DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS)

val Date.isTomorrow get() = DateUtils.isToday(time - DateUtils.DAY_IN_MILLIS)
