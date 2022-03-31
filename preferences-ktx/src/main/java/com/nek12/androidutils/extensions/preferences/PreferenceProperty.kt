package com.nek12.androidutils.extensions.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias PreferenceProvider = () -> SharedPreferences

/**
 * A sharedPreferences delegate that allows you to write one-liners for loading and saving data
 * from/to your app's default SharedPreferences.
 * Uses [SharedPreferences.Editor.apply] that does sharedpreferences operations on background thread
 * SharedPreferences is a Singleton object so you can easily get as many references as you want, it opens file only when you call getSharedPreferences first time, or create only one reference for it.
 * example:
 * ```
 *  var isFirstLaunch: Boolean = booleanPreference(KEY_FIRST_LAUNCH)
 *  if (isFirstLaunch) {
 *      //...
 *  }
 *  isFirstLaunch = false
 * ```
 */
internal abstract class PreferenceProperty<in T, V>(
    private val defaultValue: V,
    private val key: String? = null,
    private val getter: SharedPreferences.(String, V) -> V,
    private val setter: SharedPreferences.Editor.(String, V) -> SharedPreferences.Editor,
) : ReadWriteProperty<T, V> {

    abstract fun getPreferences(thisRef: T): SharedPreferences

    override fun getValue(thisRef: T, property: KProperty<*>): V =
        getPreferences(thisRef).getter(key ?: property.name, defaultValue)

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: T, property: KProperty<*>, value: V) =
        getPreferences(thisRef).edit().setter(key ?: property.name, value).apply()

}

internal class DefaultPreferenceProperty<T>(
    defaultValue: T,
    key: String? = null,
    getter: SharedPreferences.(String, T) -> T,
    setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
) : PreferenceProperty<Context, T>(defaultValue, key, getter, setter) {

    private var _prefs: SharedPreferences? = null

    override fun getPreferences(thisRef: Context): SharedPreferences =
        _prefs ?: thisRef.getDefaultPreferences().also { _prefs = it }
}

internal class ProvidedPreferenceProperty<T>(
    defaultValue: T,
    key: String? = null,
    getter: SharedPreferences.(String, T) -> T,
    setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
    private val preferences: SharedPreferences,
) : PreferenceProperty<Any?, T>(defaultValue, key, getter, setter) {

    override fun getPreferences(thisRef: Any?): SharedPreferences = preferences
}
