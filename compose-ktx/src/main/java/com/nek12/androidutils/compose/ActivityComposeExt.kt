package com.nek12.androidutils.compose

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.nek12.androidutils.extensions.android.launchCatching
import com.nek12.androidutils.extensions.android.withApiLevel

/**
 * Possible values for [orientation]:
 *
 *  * SCREEN_ORIENTATION_UNSPECIFIED,
 *  * SCREEN_ORIENTATION_LANDSCAPE,
 *  * SCREEN_ORIENTATION_PORTRAIT,
 *  * SCREEN_ORIENTATION_USER,
 *  * SCREEN_ORIENTATION_BEHIND,
 *  * SCREEN_ORIENTATION_SENSOR,
 *  * SCREEN_ORIENTATION_NOSENSOR,
 *  * SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
 *  * SCREEN_ORIENTATION_SENSOR_PORTRAIT,
 *  * SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
 *  * SCREEN_ORIENTATION_REVERSE_PORTRAIT,
 *  * SCREEN_ORIENTATION_FULL_SENSOR,
 *  * SCREEN_ORIENTATION_USER_LANDSCAPE,
 *  * SCREEN_ORIENTATION_USER_PORTRAIT,
 *  * SCREEN_ORIENTATION_FULL_USER,
 *  * SCREEN_ORIENTATION_LOCKED.
 *
 * @param orientation the int constant of [Activity.getRequestedOrientation]
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

/**
 * Traverses the context hierarchy until an activity is found, or null if not present.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Disables overlays over the content of current activity for as long as this composable is in the composition
 * Works only on API 31 (S) and above, otherwise does nothing.
 *
 * Requires permission [Manifest.permission.HIDE_OVERLAY_WINDOWS]
 * @see android.view.Window.setHideOverlayWindows
 */
@Composable
@RequiresPermission(Manifest.permission.HIDE_OVERLAY_WINDOWS)
fun DisallowOverlays() = withApiLevel(Build.VERSION_CODES.S) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        window?.setHideOverlayWindows(true)
        onDispose { window?.setHideOverlayWindows(false) }
    }
}

/**
 * Sets flags to the parent activity, if exists, temporarily, until the composition is left.
 * The flags are added and then removed, other flags are not changed
 * Possible values of [flags] are from [WindowManager.LayoutParams]
 *
 * @see [android.view.Window.setFlags]
 */
@Composable
fun SetWindowFlags(flags: Int) {
    val context = LocalContext.current
    DisposableEffect(flags) {
        val window = context.findActivity()?.window
        window?.addFlags(flags)
        onDispose { window?.clearFlags(flags) }
    }
}

/**
 * Keeps the screen of the current activity on while this composable is in composition.
 */
@Composable
fun KeepScreenOn(enabled: Boolean = true) {
    val context = LocalContext.current
    DisposableEffect(enabled) {
        val window = context.findActivity()?.window
        if (enabled) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } else {
            onDispose { }
        }
    }
}

/**
 * Registers a new lifecycle observer for the lifetime of the composition of this function, then clears it.
 */
@Composable
fun ObserveLifecycle(onEvent: (event: Lifecycle.Event) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current
    val action by rememberUpdatedState(onEvent)
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            action(event)
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * A shortcut for sending messages to any social media app installed in the
 * user's device. This will optionally return an activity result as it's why an
 * extension for [ManagedActivityResultLauncher].
 *
 * @param text the message to send.
 * @param onAppNotFound a callback for error, if ever no apps are installed that can handle the request.
 */

inline fun ManagedActivityResultLauncher<Intent, ActivityResult>.shareAsText(
    text: String,
    onAppNotFound: (e: Exception) -> Unit
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    launchCatching(
        input = shareIntent,
        onNotFound = onAppNotFound,
    )
}
