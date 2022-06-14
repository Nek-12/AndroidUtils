package com.nek12.androidutils.extensions.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

inline fun <reified T: Activity> Context.intent(vararg extras: Pair<KProperty1<T, *>, Any?>): Intent {
    return intentFor<T>(this).setExtras(extras = extras)
}

inline fun <reified T: Any> intentFor(context: Context): Intent = Intent(context, T::class.java)

fun <T: Activity> Intent.setExtras(vararg extras: Pair<KProperty1<T, Any?>, Any?>): Intent = apply {
    putExtras(bundleOf(pairs = extras.map { it.first.name to it.second }.toTypedArray()))
}

inline fun <reified T> Activity.extra(defaultValue: T? = null) =
    object: BundleExtra<Activity, T>(null is T, defaultValue) {
        override val bundle: Bundle? get() = intent.extras
    }

@Suppress("UNCHECKED_CAST")
abstract class BundleExtra<in T, V>(
    private val isNullable: Boolean,
    private val defaultValue: V?,
): ReadOnlyProperty<T, V> {

    abstract val bundle: Bundle?

    private var internalValue: Any? = UNINITIALIZED_VALUE

    override fun getValue(thisRef: T, property: KProperty<*>): V = when (internalValue) {
        is UNINITIALIZED_VALUE -> {
            internalValue = bundle?.get(property.name)
                ?: defaultValue
                        ?: if (isNullable)
                    null
                else
                    throw IllegalArgumentException("Required value was not provided: ${property.name}")
        }
        else -> internalValue
    } as V
}

@Suppress("ClassName")
private object UNINITIALIZED_VALUE

inline fun <reified T> Intent.requireExtra(key: String) =
    requireNotNull(extras?.get(key)) { "required extra \"$key\" not provided" } as T
