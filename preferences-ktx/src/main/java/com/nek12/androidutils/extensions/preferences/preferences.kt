@file:Suppress("unused")

package com.nek12.androidutils.extensions.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias PreferenceProvider = (Context) -> SharedPreferences

/**
 * A sharedPreferences delegate that allows you to write one-liners for loading and saving data
 * from/to your app's default SharedPreferences.
 * Uses [SharedPreferences.Editor.apply] that does sharedpreferences operations on background thread
 * example:
 * ```
 *  var isFirstLaunch: Boolean = booleanPreference(KEY_FIRST_LAUNCH)
 *  if (isFirstLaunch) {
 *      //...
 *  }
 *  isFirstLaunch = false
 * ```
 */
class PreferenceProperty<T>(
    private val key: String,
    private val defaultValue: T,
    private val getter: SharedPreferences.(String, T) -> T,
    private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
    private val preferenceProvider: PreferenceProvider = Context::getPreferences
) : ReadWriteProperty<Context, T> {

    override fun getValue(thisRef: Context, property: KProperty<*>): T =
        preferenceProvider(thisRef).getter(key, defaultValue)

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: Context, property: KProperty<*>, value: T) =
        preferenceProvider(thisRef).edit().setter(key, value).apply()
}

/**
 * Obtains default shared preferences for this application
 */
fun Context.getPreferences(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(this)

fun intPreference(
    key: String,
    defaultValue: Int = 0,
    provider: PreferenceProvider = Context::getPreferences
): ReadWriteProperty<Context, Int> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt, provider)

fun stringPreference(
    key: String,
    defaultValue: String? = null,
    provider: PreferenceProvider = Context::getPreferences
): ReadWriteProperty<Context, String?> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString, provider)

fun booleanPreference(
    key: String, defaultValue: Boolean = false,
    provider: PreferenceProvider = Context::getPreferences
): ReadWriteProperty<Context, Boolean> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean, provider)

fun floatPreference(
    key: String,
    defaultValue: Float = 0f,
    provider: PreferenceProvider = Context::getPreferences
): ReadWriteProperty<Context, Float> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat, provider)

fun longPreference(
    key: String,
    defaultValue: Long = 0L,
    provider: PreferenceProvider = Context::getPreferences
): ReadWriteProperty<Context, Long> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong, provider)
