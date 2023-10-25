@file:Suppress("NOTHING_TO_INLINE")

package com.nek12.androidutils.extensions.android

import androidx.annotation.StringRes

sealed interface Text {

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    @JvmInline
    value class Dynamic(val text: String) : Text {

        override fun toString() = "Text.Dynamic(text=$text)"
    }

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

inline fun String.text() = Text.Dynamic(this)
inline fun Int.text(vararg args: Any) = Text.Resource(this, args = args)
