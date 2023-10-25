@file:Suppress("unused")

package com.nek12.androidutils.extensions.android

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Application
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.text.format.DateFormat
import android.view.autofill.AutofillManager
import android.webkit.CookieManager
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.os.bundleOf

private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"

@PublishedApi
internal const val EXTRA_SYSTEM_ALERT_WINDOW = "system_alert_window"

/**
 * @param numberUri uri of the form tel:+1234567890, containing countryCode
 */
inline fun Context.dialNumber(numberUri: Uri, onNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_DIAL, numberUri)
    startActivityCatching(intent, onNotFound)
}

inline fun <I> ActivityResultLauncher<I>.launchCatching(
    input: I,
    options: ActivityOptionsCompat? = null,
    onNotFound: (ActivityNotFoundException) -> Unit,
) {
    try {
        launch(input, options)
    } catch (e: ActivityNotFoundException) {
        onNotFound(e)
    }
}


inline fun Context.startActivityCatching(intent: Intent, onNotFound: (Exception) -> Unit) {
    try {
        startActivity(intent)
    } catch (expected: Exception) {
        onNotFound(expected)
    }
}

/**
 * Saves file using [DownloadManager] to users /sdcard/Downloads/
 * @param onFailure is called if there was an exception. Possible exceptions are:
 *     - ActivityNotFoundException
 *     - SecurityException - when permission to write to storage was not granted
 *     - IllegalStateException - when provided parameters are invalid (i.e. download directory can't be created)
 * */
inline fun Context.downloadFile(
    url: Uri,
    fileName: String,
    userAgent: String? = null,
    description: String? = null,
    mimeType: String? = null,
    onFailure: (e: Exception) -> Unit,
) {
    val request = DownloadManager.Request(url).apply {
        val cookies = CookieManager.getInstance().getCookie(url.toString())
        addRequestHeader("cookie", cookies)
        addRequestHeader("User-Agent", userAgent)
        if (description != null) setDescription(description)
        setTitle(fileName)
        setMimeType(mimeType)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
    }
    try {
        ContextCompat.getSystemService(
            this, DownloadManager::class.java
        )?.enqueue(request) ?: throw ActivityNotFoundException("DownloadManager not found")
    } catch (expected: Exception) {
        onFailure(expected)
        return
    }
}

inline fun Context.openBrowser(url: Uri, onAppNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_VIEW, url).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }
    startActivityCatching(intent, onAppNotFound)
}

inline fun Context.shareAsText(text: String, onAppNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivityCatching(shareIntent, onAppNotFound)
}

inline fun Context.sendEmail(mail: Email, onAppNotFound: (e: Exception) -> Unit) {
    // Use SENDTO to avoid showing pickers and letting non-email apps interfere
    val sendIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        // EXTRA_EMAIL should be array
        putExtra(Intent.EXTRA_EMAIL, mail.recipients?.toTypedArray())
        putExtra(Intent.EXTRA_SUBJECT, mail.subject)
        putExtra(Intent.EXTRA_TEXT, mail.body)
    }
    startActivityCatching(sendIntent, onAppNotFound)
}

val Context.autofillManager
    get() = withApiLevel(VERSION_CODES.O, below = { null }) { getSystemService<AutofillManager>() }

fun Application.relaunch() {
    // Obtain the startup Intent of the application with the package name of the application
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)?.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}

val Context.isSystem24Hour get() = DateFormat.is24HourFormat(this)

fun Uri.asDeeplinkIntent(
    requestCode: Int,
    context: Context
) = TaskStackBuilder.create(context).run {
    addNextIntentWithParentStack(intent().allowBackgroundActivityStart())
    getPendingIntent(
        requestCode,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )!!
}

/**
 * Returns an URI to [this] raw resource id
 */
fun Int.raw(context: Context): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(context.applicationContext.packageName)
    .appendPath(toString())
    .build()

@RequiresApi(VERSION_CODES.O)
fun Context.openNotificationSettings(onError: (Exception) -> Unit) = startActivityCatching(
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    },
    onNotFound = onError,
)

val Context.packageUri: Uri get() = "package:$packageName".toUri()

fun Context.openAppDetails(onError: (Exception) -> Unit) = startActivityCatching(
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri),
    onNotFound = onError,
)

inline fun Context.openSystemOverlaysSettings(onError: (Exception) -> Unit) {
    startActivityCatching(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, packageUri).highlightSetting(EXTRA_SYSTEM_ALERT_WINDOW),
        onNotFound = onError,
    )
}

/**
 * Changes the uri pointing to settings to highlight selected entry
 * @param settingName: Key for permission:
 * https://cs.android.com/android/platform/superproject/+/master:packages/apps/Settings/res/xml/app_info_settings.xml?q=preferred_settings&ss=android%2Fplatform%2Fsuperproject
 */
@PublishedApi
internal fun Intent.highlightSetting(settingName: String) = apply {
    putExtra(EXTRA_FRAGMENT_ARG_KEY, settingName)
    val bundle = bundleOf(EXTRA_FRAGMENT_ARG_KEY to settingName)
    putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
}

@SuppressLint("BatteryLife")
inline fun Context.requestIgnoreBatteryOptimization(onError: (Exception) -> Unit) = startActivityCatching(
    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, packageUri),
    onNotFound = onError,
)

@ChecksSdkIntAtLeast(parameter = 0, lambda = 2)
inline fun withApiLevel(versionCode: Int, since: () -> Unit) {
    if (Build.VERSION.SDK_INT >= versionCode) since()
}

@ChecksSdkIntAtLeast(parameter = 0, lambda = 2)
inline fun <R> withApiLevel(versionCode: Int, below: () -> R, since: () -> R) =
    if (Build.VERSION.SDK_INT >= versionCode) since() else below()

@RequiresApi(VERSION_CODES.S)
inline fun Context.requestExactAlarmPermission(onError: (Exception) -> Unit) =
    startActivityCatching(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM), onNotFound = onError)

inline fun <T> Context.withPermission(
    permission: String,
    ifDenied: Context.(String) -> T,
    ifGranted: Context.(String) -> T
) = if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
    ifGranted(permission) else ifDenied(permission)

fun Uri.intent() = Intent(Intent.ACTION_VIEW, this)


/**
 * Allows background activity start for this intent starting with SDK 34 (Upside down cake).
 * On previous versions, does nothing.
 */
fun Intent.allowBackgroundActivityStart() = apply {
    withApiLevel(VERSION_CODES.UPSIDE_DOWN_CAKE, {}) {
        ActivityOptions
            .makeBasic()
            .setPendingIntentCreatorBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            .toBundle()
            .let(::putExtras)
    }
}

fun Context.getGooglePlayUri() = "https://play.google.com/store/apps/details?id=${applicationInfo.packageName}"

inline fun Context.openAppPlayStorePage(onNotFound: (e: Exception) -> Unit) =
    startActivityCatching(getGooglePlayUri().toUri().intent(), onNotFound)
