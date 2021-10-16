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


fun Resources.getDimenInDP(@DimenRes id: Int): Int {
    return (getDimension(id) / displayMetrics.density).toInt()
}

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


fun Context.resIdByName(resIdName: String, resType: String): Int {
    return resources.getIdentifier(resIdName, resType, packageName)
}

fun Int.asColor(context: Context) = ContextCompat.getColor(context, this)
fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context,this)

fun Activity.uriToBitmap(uri: Uri): Bitmap? {
    val bytes = contentResolver.openInputStream(uri)?.readBytes()
    return if (bytes == null) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
