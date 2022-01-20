package com.nek12.androidutils.extensions.android

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.CookieManager
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
        Log.e("StartActivityCatching", "Activity not found", e)
        onNotFound(e)
    }
}

/**
 * @param onAppNotFound is called if there is no download manager on user's device.
 * */
fun Context.downloadFile(
    url: Uri,
    fileName: String,
    userAgent: String? = null,
    description: String? = null,
    mimeType: String? = null,
    onAppNotFound: (e: ActivityNotFoundException) -> Unit,
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
    } catch (e: ActivityNotFoundException) {
        onAppNotFound(e)
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
    val sendIntent: Intent = Intent(Intent.ACTION_SEND).apply {
        //EXTRA_EMAIL should be array
        putExtra(Intent.EXTRA_EMAIL, mail.recipients?.toTypedArray())
        putExtra(Intent.EXTRA_SUBJECT, mail.subject)
        putExtra(Intent.EXTRA_TEXT, mail.body)
        //RFC standard for email
        type = "message/rfc822"
    }
    startActivityCatching(sendIntent, onAppNotFound)
}
