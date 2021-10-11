@file:Suppress("unused")

package com.nek12.androidutils.recyclerview

import android.util.Log
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

internal const val CAST_MESSAGE = """
    Could not bind your Item because you provided wrong layout|type argument pair
"""
internal const val TAG = "GenericAdapter"

typealias Binder<T, VB> = (BindPayload<T, VB>) -> Unit

/**
 * When overriding, add necessary constructor parameters yourself
 * like val item: T
 */
abstract class Item<T, VB : ViewDataBinding> {
    abstract val data: T
    abstract val id: Long
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    @get:LayoutRes
    abstract val layout: Int

    open fun bind(binding: VB, bindingPos: Int) {}

    internal fun tryBind(binding: ViewDataBinding, bindingPos: Int) =
        cast(binding)?.let { bind(it, bindingPos) } ?: Log.e(TAG, CAST_MESSAGE)

    @Suppress("UNCHECKED_CAST")
    private fun cast(viewDataBinding: ViewDataBinding): VB? = viewDataBinding as? VB
}

abstract class BlankItem<VB : ViewDataBinding> : Item<Unit, VB>() {
    override val data: Unit = Unit
    override val id = 0L
}

data class GenericItem<T, VB : ViewDataBinding>(
    override val data: T,
    override val id: Long,
    override val layout: Int,
    val binder: Binder<T, VB>? = null,
) : Item<T, VB>() {
    override fun bind(binding: VB, bindingPos: Int) {
        binder?.invoke(BindPayload(this, binding, bindingPos))
    }
}

data class BindPayload<T, VB : ViewDataBinding>(
    val item: Item<T, VB>,
    val binding: VB,
    val bindingPos: Int,
) {
    val data: T get() = item.data
}
