package com.nek12.androidutils.extensions.android

import android.app.Application
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.view.autofill.AutofillManager
import android.webkit.CookieManager
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat

/**
 * @param numberUri uri of the form tel:+1234567890, containing countryCode
 */
fun Context.dialNumber(numberUri: Uri, onNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_DIAL, numberUri)
    startActivityCatching(intent, onNotFound)
}

fun Context.startActivityCatching(intent: Intent, onNotFound: (Exception) -> Unit) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("StartActivityCatching", "Exception: ", e)
        onNotFound(e)
    }
}

/**
 * Saves file using [DownloadManager] to users /sdcard/Downloads/
 * @param onFailure is called if there was an exception. Possible exceptions are:
 *     - ActivityNotFoundException
 *     - SecurityException - when permission to write to storage was not granted
 *     - IllegalStateException - when provided parameters are invalid (i.e. download directory can't be created)
 * */
fun Context.downloadFile(
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
    } catch (e: Exception) {
        onFailure(e)
        return
    }
}

fun Context.openBrowser(url: Uri, onAppNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_VIEW, url)
    startActivityCatching(intent, onAppNotFound)
}

fun Context.shareAsText(text: String, onAppNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivityCatching(shareIntent, onAppNotFound)
}

fun Context.sendEmail(mail: Email, onAppNotFound: (e: Exception) -> Unit) {
    //Use SENDTO to avoid showing pickers and letting non-email apps interfere
    val sendIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
        //EXTRA_EMAIL should be array
        putExtra(Intent.EXTRA_EMAIL, mail.recipients?.toTypedArray())
        putExtra(Intent.EXTRA_SUBJECT, mail.subject)
        putExtra(Intent.EXTRA_TEXT, mail.body)
        //RFC standard for email
        type = "message/rfc822"
    }
    startActivityCatching(sendIntent, onAppNotFound)
}

val Context.autofillManager
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getSystemService(AutofillManager::class.java)
    } else {
        null
    }

fun Application.relaunch() {
    //Obtain the startup Intent of the application with the package name of the application
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)?.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}

val Context.isSystem24Hour get() = DateFormat.is24HourFormat(this)

inline fun <reified T> Context.makeDeeplinkIntent(uri: Uri): PendingIntent {

    val intent = Intent(
        Intent.ACTION_VIEW,
        uri,
        this,
        T::class.java
    )

    return TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!
    }
}
