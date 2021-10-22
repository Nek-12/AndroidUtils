@file:Suppress("unused")

package com.nek12.androidutils.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

/**
 * Get dimension dp value from your xml.
 * When you use [Resources.getDimension] you get the amount of pixels for that dimen.
 * This function returns a proper dp value just like what you wrote in your dimen.xml
 */
fun Resources.getDimenInDP(@DimenRes id: Int): Int {
    return (getDimension(id) / displayMetrics.density).toInt()
}

/**
 * Set the image drawable for this [ImageView] using [avdResId], then start animating it.
 * The animation runs in loops and never stops.
 */
fun ImageView.applyLoopingAnimatedVectorDrawable(@DrawableRes avdResId: Int) {
    val animated = ResourcesCompat.getDrawable(resources, avdResId, context.theme) as? AnimatedVectorDrawableCompat
    animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            this@applyLoopingAnimatedVectorDrawable.post { animated.start() }
        }
    })
    this.setImageDrawable(animated)
    animated?.start()
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
fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context,this)

/**
 * Synchronously parses this uri and returns a bitmap.
 */
@Deprecated("Main thread heavy operation", ReplaceWith("An asynchronous loading api"))
fun Activity.uriToBitmap(uri: Uri): Bitmap? {
    val bytes = contentResolver.openInputStream(uri)?.readBytes()
    return if (bytes == null) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
