package com.nek12.androidutils.extensions.android

import androidx.annotation.StringRes

sealed class Text {

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    data class Dynamic(val text: String) : Text() {

        override fun toString() = "Text.Dynamic(text=$text)"
    }

    class Resource(@StringRes val id: Int, vararg val args: Any) : Text() {

        fun copy(id: Int, vararg args: Any) = Resource(id, args = args.takeIf { it.isNotEmpty() } ?: this.args)

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

        override fun toString(): String = "Text.Resource(id=$id, args=${args.contentToString()})"
    }
}

fun String.text() = Text.Dynamic(this)
fun Int.text(vararg args: Any) = Text.Resource(this, args = args)
