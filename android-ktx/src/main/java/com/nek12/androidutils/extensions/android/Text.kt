@file:Suppress("NOTHING_TO_INLINE")

package com.nek12.androidutils.extensions.android

import androidx.annotation.StringRes

internal const val Deprecation = """
Resource wrappers are now deprecated as they are not compatible with KMP and are leaky abstractions
Please use multiplatform-resources or a similar library. Don't pass resources to the business logic.
"""

@Deprecated(Deprecation)
sealed interface Text {

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    @JvmInline
    @Deprecated(Deprecation)
    value class Dynamic(val text: String) : Text {

        override fun toString() = "Text.Dynamic(text=$text)"
    }

    @Suppress("UseDataClass") // vararg arguments are not supported for data classes
    @Deprecated(Deprecation)
    class Resource(@StringRes val id: Int, vararg val args: Any) : Text {

        fun copy(id: Int, vararg args: Any) = Resource(id, args = args.takeIf { it.isNotEmpty() } ?: this.args)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Resource

            if (id != other.id) return false
            return args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + args.contentHashCode()
            return result
        }

        override fun toString(): String = "Text.Resource(id=$id, args=${args.contentToString()})"
    }
}

@Deprecated(Deprecation)
fun String.text() = Text.Dynamic(this)

@Deprecated(Deprecation)
fun Int.text(vararg args: Any) = Text.Resource(this, args = args)
