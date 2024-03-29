package com.nek12.androidutils.compose

import androidx.compose.runtime.Immutable

/**
 * A wrapper class that marks this type to be stable.
 * Often used for 3rd-party dependencies that cannot be marked as `@Immutable` but are guaranteed to adhere to the
 * contract
 */
@Deprecated("Use the Compose Stability Configuration File")
@JvmInline
@Immutable
value class Stable<T>(val value: T)

@Deprecated("Use the Compose Stability Configuration File")
val <T> T.stable get() = Stable(this)
