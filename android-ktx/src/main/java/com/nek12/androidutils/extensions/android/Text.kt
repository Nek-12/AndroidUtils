package com.nek12.androidutils.extensions.android

import androidx.annotation.StringRes
import kotlin.DeprecationLevel.WARNING

internal const val TEXT_DEPRECATION_MESSAGE = """
      Using resource wrappers is discouraged because it inhibits SoC and multiplatform compatibility.
      Use UI-level resolution instead
"""

@Deprecated(TEXT_DEPRECATION_MESSAGE, level = WARNING)
sealed class Text {

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    data class Dynamic(val text: String) : Text()

    class Resource(@StringRes val id: Int, vararg val args: Any) : Text() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Resource

            if (id != other.id) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + args.contentHashCode()
            return result
        }

        override fun toString(): String = "TextResource.Resource(id=$id, args=${args.contentToString()})"
    }
}
