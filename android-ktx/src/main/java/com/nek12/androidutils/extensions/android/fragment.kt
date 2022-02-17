package com.nek12.androidutils.extensions.android

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment

/**
 * Whether the device is in landscape mode right now
 */
val Fragment.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Fragment.doOnBackPressed(action: OnBackPressedCallback) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, action)
}

fun Fragment.doOnBackPressed(action: OnBackPressedCallback.() -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true, action)
}

fun Fragment.sendEmail(mail: Email, onNotFound: (e: Exception) -> Unit) = requireContext().sendEmail(mail, onNotFound)

fun Fragment.sendEmail(uri: Uri, onNotFound: (e: Exception) -> Unit) = sendEmail(Email.ofUri(uri), onNotFound)

fun Fragment.shareAsText(text: String, onAppNotFound: (e: Exception) -> Unit) =
    requireContext().shareAsText(text, onAppNotFound)

fun Fragment.openBrowser(url: Uri, onAppNotFound: (e: Exception) -> Unit) =
    requireContext().openBrowser(url, onAppNotFound)

fun Fragment.downloadFile(
    url: Uri,
    fileName: String,
    userAgent: String? = null,
    description: String? = null,
    mimeType: String? = null,
    onAppNotFound: (e: Exception) -> Unit,
) {
    requireContext().downloadFile(url, fileName, userAgent, description, mimeType, onAppNotFound)
}

fun Fragment.startActivityCatching(intent: Intent, onNotFound: (Exception) -> Unit) =
    requireContext().startActivityCatching(intent, onNotFound)

fun Fragment.dialNumber(numberUri: Uri, onNotFound: (e: Exception) -> Unit) =
    requireContext().dialNumber(numberUri, onNotFound)

val Fragment.autofillManager get() = requireContext().autofillManager

fun Fragment.drawableCompat(@DrawableRes id: Int): Drawable {
    return requireContext().getDrawableCompat(id)
}
