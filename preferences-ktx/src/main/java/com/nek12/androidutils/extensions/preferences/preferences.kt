@file:Suppress("unused")

package com.nek12.androidutils.extensions.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A sharedPreferences delegate that allows you to write one-liners for loading and saving data
 * from/to your app's default SharedPreferences.
 * **Despite looking innocent, this is still a shared preference implementation and thus it
 * does heavy IO on the main thread. Do not abuse this simplicity. **
 * example:
 * ```
 *  var isFirstLaunch: Boolean = booleanPreference(KEY_FIRST_LAUNCH)
 *  if (isFirstLaunch) {  //does IO on the main thread
 *      //...
 *  }
 *  isFirstLaunch = false //does IO on the main thread
 * ```
 */
class PreferenceProperty<T>(
    private val key: String,
    private val defaultValue: T,
    private val getter: SharedPreferences.(String, T) -> T,
    private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
) : ReadWriteProperty<Context, T> {

    override fun getValue(thisRef: Context, property: KProperty<*>): T =
        thisRef.getPreferences().getter(key, defaultValue)

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: Context, property: KProperty<*>, value: T) =
        thisRef.getPreferences().edit().setter(key, value).apply()

    private fun Context.getPreferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this)
}

fun intPreference(key: String, defaultValue: Int = 0): ReadWriteProperty<Context, Int> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun stringPreference(key: String, defaultValue: String? = null): ReadWriteProperty<Context, String?> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString)

fun booleanPreference(key: String, defaultValue: Boolean = false): ReadWriteProperty<Context, Boolean> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun floatPreference(key: String, defaultValue: Float = 0f): ReadWriteProperty<Context, Float> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

fun longPreference(key: String, defaultValue: Long = 0L): ReadWriteProperty<Context, Long> =
    PreferenceProperty(key, defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
