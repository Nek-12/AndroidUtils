@file:Suppress("unused")

package com.nek12.androidutils.extensions.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty

/**
 * Obtains default shared preferences for this application
 */
fun Context.getDefaultPreferences(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(this)

fun intPreference(
    key: String,
    defaultValue: Int = 0,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, Int> =
    ProvidedPreferenceProperty(key, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt, provider)

fun stringPreference(
    key: String,
    defaultValue: String? = null,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, String?> =
    ProvidedPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getString,
        SharedPreferences.Editor::putString,
        provider
    )

fun booleanPreference(
    key: String,
    defaultValue: Boolean = false,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, Boolean> =
    ProvidedPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getBoolean,
        SharedPreferences.Editor::putBoolean,
        provider
    )

fun floatPreference(
    key: String,
    defaultValue: Float = 0f,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, Float> =
    ProvidedPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getFloat,
        SharedPreferences.Editor::putFloat,
        provider
    )

fun longPreference(
    key: String,
    defaultValue: Long = 0L,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, Long> =
    ProvidedPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getLong,
        SharedPreferences.Editor::putLong,
        provider
    )

/**
 * @return [defaultValue] if base [SharedPreferences.getString] returned `null`
 */
@JvmName("stringPreferenceNotNull")
fun stringPreference(
    key: String,
    defaultValue: String,
    provider: PreferenceProvider
): ReadWriteProperty<Any?, String> =
    ProvidedPreferenceProperty(
        key,
        defaultValue,
        { k, default -> getString(k, default) ?: defaultValue },
        SharedPreferences.Editor::putString, provider
    )

//Default preferences

fun intPreference(
    key: String,
    defaultValue: Int = 0,
): ReadWriteProperty<Context, Int> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getInt,
        SharedPreferences.Editor::putInt
    )

fun stringPreference(
    key: String,
    defaultValue: String? = null,
): ReadWriteProperty<Context, String?> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getString,
        SharedPreferences.Editor::putString,
    )

fun booleanPreference(
    key: String,
    defaultValue: Boolean = false,
): ReadWriteProperty<Context, Boolean> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getBoolean,
        SharedPreferences.Editor::putBoolean,
    )

fun floatPreference(
    key: String,
    defaultValue: Float = 0f,
): ReadWriteProperty<Context, Float> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getFloat,
        SharedPreferences.Editor::putFloat,
    )

fun longPreference(
    key: String,
    defaultValue: Long = 0L,
): ReadWriteProperty<Context, Long> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        SharedPreferences::getLong,
        SharedPreferences.Editor::putLong,
    )

/**
 * @return [defaultValue] if base [SharedPreferences.getString] returned `null`
 */
@JvmName("stringPreferenceNotNull")
fun stringPreference(
    key: String,
    defaultValue: String,
): ReadWriteProperty<Context, String> =
    DefaultPreferenceProperty(
        key,
        defaultValue,
        { k, default -> getString(k, default) ?: defaultValue },
        SharedPreferences.Editor::putString
    )
