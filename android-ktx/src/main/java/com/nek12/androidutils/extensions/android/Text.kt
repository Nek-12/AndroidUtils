package com.nek12.androidutils.extensions.android

import android.content.Context
import androidx.annotation.StringRes

sealed class Text {

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    data class Dynamic(val text: String): Text()

    class Resource(@StringRes val id: Int, vararg val args: Any): Text() {

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

        override fun toString(): String {
            return "TextResource.Resource(id=$id, args=${args.contentToString()})"
        }
    }

    fun string(context: Context): String = when (this@Text) {
        is Dynamic -> text
        is Resource -> context.getString(id, *args)
    }
}
