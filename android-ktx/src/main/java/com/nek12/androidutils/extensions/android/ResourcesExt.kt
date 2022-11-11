@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import java.util.*

/**
 * Get dimension dp value from your xml.
 * When you use [Resources.getDimension] you get the amount of pixels for that dimen.
 * This function returns a proper dp value just like what you wrote in your dimen.xml
 */
fun Resources.getDimenInDP(id: Int): Int {
    return (getDimension(id) / displayMetrics.density).toInt()
}

/**
 * Rescales the bitmap
 * @param maxSize The maximum size of the longest side of the image (can be either height or width) in pixels
 * @return scaled bitmap
 */
fun Bitmap.scale(maxSize: Int): Bitmap {
    val ratio = width.toFloat() / height.toFloat()
    var newWidth = maxSize
    var newHeight = maxSize
    if (ratio > 1) {
        newHeight = (maxSize / ratio).toInt()
    } else {
        newWidth = (maxSize * ratio).toInt()
    }
    return Bitmap.createScaledBitmap(this@scale, newWidth, newHeight, true)
}

/**
 * Returns an id of the resource by its name as you wrote it in the xml
 * @see Resources.getIdentifier
 */
fun Context.resIdByName(resIdName: String, resType: String): Int {
    return resources.getIdentifier(resIdName, resType, packageName)
}

/**
 * Uses the value of this int as a **resource id** to parse an [android.graphics.Color] object
 */
fun Int.asColor(context: Context) = ContextCompat.getColor(context, this)

/**
 * Uses this int as a **resource id** to get a drawable
 */
fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context, this)

val Resources.currentLocale: Locale
    get() = ConfigurationCompat.getLocales(configuration).get(0)!!
