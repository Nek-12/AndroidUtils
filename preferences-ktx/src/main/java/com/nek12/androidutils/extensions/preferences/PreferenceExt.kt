@file:Suppress("unused")

package com.nek12.androidutils.extensions.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.time.Instant
import kotlin.properties.ReadWriteProperty

/**
 * Obtains default shared preferences for this application
 */
fun Context.defaultPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

fun intPreference(
    preferences: SharedPreferences,
    defaultValue: Int = 0,
    key: String? = null,
): ReadWriteProperty<Any?, Int> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getInt,
    SharedPreferences.Editor::putInt,
    preferences
)

fun stringPreference(
    preferences: SharedPreferences,
    defaultValue: String? = null,
    key: String? = null,
): ReadWriteProperty<Any?, String?> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getString,
    SharedPreferences.Editor::putString,
    preferences
)

fun booleanPreference(
    preferences: SharedPreferences,
    defaultValue: Boolean = false,
    key: String? = null,
): ReadWriteProperty<Any?, Boolean> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getBoolean,
    SharedPreferences.Editor::putBoolean,
    preferences
)

fun floatPreference(
    preferences: SharedPreferences,
    defaultValue: Float = 0f,
    key: String? = null,
): ReadWriteProperty<Any?, Float> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getFloat,
    SharedPreferences.Editor::putFloat,
    preferences
)

fun longPreference(
    preferences: SharedPreferences,
    defaultValue: Long = 0L,
    key: String? = null,
): ReadWriteProperty<Any?, Long> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getLong,
    SharedPreferences.Editor::putLong,
    preferences
)

/**
 * @return [defaultValue] if base [SharedPreferences.getString] returned `null`
 */
@JvmName("stringPreferenceNotNull")
fun stringPreference(
    preferences: SharedPreferences,
    defaultValue: String,
    key: String? = null,
): ReadWriteProperty<Any?, String> = ProvidedPreferenceProperty(
    defaultValue,
    key,
    { k, default -> getString(k, default) ?: defaultValue },
    SharedPreferences.Editor::putString,
    preferences
)

@SuppressLint("NewApi")
fun instantPreference(
    preferences: SharedPreferences,
    defaultValue: Instant,
    key: String? = null,
) = ProvidedPreferenceProperty(
    defaultValue = defaultValue,
    key = key,
    setter = { k, v -> putLong(k, v.toEpochMilli()) },
    getter = { k, v -> Instant.ofEpochMilli(getLong(k, v.toEpochMilli())) },
    preferences = preferences,
)

inline fun <reified T : Enum<T>> enumStringPreference(
    preferences: SharedPreferences,
    defaultValue: T,
    key: String? = null,
) = ProvidedPreferenceProperty(
    defaultValue = defaultValue,
    key = key,
    getter = { k, def -> getString(k, def.name)?.let { enumValueOf<T>(it) } ?: def },
    setter = { k, v -> putString(k, v.name) },
    preferences = preferences,
)

// ------------------  Default preferences

fun intPreference(
    defaultValue: Int = 0,
    key: String? = null,
): ReadWriteProperty<Context, Int> = DefaultPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getInt,
    SharedPreferences.Editor::putInt
)

fun stringPreference(
    defaultValue: String? = null,
    key: String? = null,
): ReadWriteProperty<Context, String?> = DefaultPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getString,
    SharedPreferences.Editor::putString,
)

fun booleanPreference(
    defaultValue: Boolean = false,
    key: String? = null,
): ReadWriteProperty<Context, Boolean> = DefaultPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getBoolean,
    SharedPreferences.Editor::putBoolean,
)

fun floatPreference(
    defaultValue: Float = 0f,
    key: String? = null,
): ReadWriteProperty<Context, Float> = DefaultPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getFloat,
    SharedPreferences.Editor::putFloat,
)

fun longPreference(
    defaultValue: Long = 0L,
    key: String? = null,
): ReadWriteProperty<Context, Long> = DefaultPreferenceProperty(
    defaultValue,
    key,
    SharedPreferences::getLong,
    SharedPreferences.Editor::putLong,
)

/**
 * @return [defaultValue] if base [SharedPreferences.getString] returned `null`
 */
@JvmName("stringPreferenceNotNull")
fun stringPreference(
    defaultValue: String,
    key: String? = null,
): ReadWriteProperty<Context, String> = DefaultPreferenceProperty(
    defaultValue,
    key,
    { k, default -> getString(k, default) ?: defaultValue },
    SharedPreferences.Editor::putString
)

@JvmName("enumStringPreferenceNullable")
inline fun <reified T : Enum<T>> enumStringPreference(
    preferences: SharedPreferences,
    defaultValue: T?,
    key: String? = null,
) = ProvidedPreferenceProperty(
    defaultValue = defaultValue,
    key = key,
    getter = { k, def -> getString(k, def?.name)?.let { enumValueOf<T>(it) } },
    setter = { k, v -> putString(k, v?.name) },
    preferences = preferences,
)
