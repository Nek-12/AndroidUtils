package com.nek12.androidutils.extensions.android

import android.content.res.Configuration
import androidx.fragment.app.Fragment

/**
 * Whether the device is in landscape mode right now
 */
val Fragment.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
