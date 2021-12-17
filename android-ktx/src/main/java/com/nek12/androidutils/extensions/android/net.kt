package com.nek12.androidutils.extensions.android

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.CookieManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.net.MailTo
import androidx.fragment.app.Fragment

val Uri.asHttps: Uri get() = if (scheme == "http") buildUpon().scheme("https").build() else this
val String?.asUri get() = this?.let { Uri.parse(it) }

val Uri.linkType
    get() = when (this.scheme) {
        null -> LinkType.Unknown
        "http", "https" -> LinkType.Web
        "mailto" -> LinkType.Mail
        "tel" -> LinkType.Tel
        "blob" -> LinkType.Blob
        "content" -> LinkType.ContentProvider
        "dns" -> LinkType.Dns
        "drm" -> LinkType.Drm
        "fax" -> LinkType.Fax
        "geo" -> LinkType.Geo
        "magnet" -> LinkType.Magnet
        "maps" -> LinkType.Map
        "market" -> LinkType.GooglePlay
        "messgage" -> LinkType.AppleMail
        "mms", "sms" -> LinkType.TextMessage
        "query" -> LinkType.FilesystemQuery
        "resource" -> LinkType.Resource
        "skype", "callto" -> LinkType.Skype
        "ssh" -> LinkType.Ssh
        "webcal" -> LinkType.Calendar
        "file" -> LinkType.File
        else -> LinkType.Other
    }

enum class LinkType {
    Web, Mail, Tel, Other, Unknown, Blob, ContentProvider, Dns, Drm, File, Calendar, Ssh, Skype, Resource, FilesystemQuery, TextMessage, AppleMail, GooglePlay, Map, Magnet, Geo, Fax
}


fun Context.startActivityCatching(intent: Intent, onNotFound: (Exception) -> Unit) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("StartActivityCatching", null, e)
        onNotFound(e)
    }
}

/**
 * onNotFound is called if there is no download manager on user's device.
 * */
fun Context.downloadFile(
    url: Uri,
    fileName: String,
    userAgent: String? = null,
    description: String? = null,
    mimeType: String? = null,
    onNotFound: (e: ActivityNotFoundException /* = java.lang.Exception */) -> Unit
) {
    val request = DownloadManager.Request(url).apply {
        val cookies = CookieManager.getInstance().getCookie(url.toString())
        addRequestHeader("cookie", cookies)
        addRequestHeader("User-Agent", userAgent)
        if (description != null)
            setDescription(description)
        setTitle(fileName)
        setMimeType(mimeType)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
    }
    try {
        ContextCompat.getSystemService(
            this,
            DownloadManager::class.java
        )?.enqueue(request) ?: throw ActivityNotFoundException("DownloadManager not found")
    } catch (e: ActivityNotFoundException) {
        onNotFound(e)
        return
    }
}

fun Context.openBrowser(url: Uri, onNotFound: (e: Exception) -> Unit) {
    //Try to let the browser handle this
    val intent = Intent(Intent.ACTION_VIEW, url)
    startActivityCatching(intent, onNotFound)
}

fun Fragment.downloadFile(
    url: Uri,
    fileName: String,
    userAgent: String? = null,
    description: String? = null,
    mimeType: String? = null,
    onNotFound: (e: ActivityNotFoundException /* = java.lang.Exception */) -> Unit
) {
    requireContext().downloadFile(url, fileName, userAgent, description, mimeType, onNotFound)
}

fun Fragment.openBrowser(url: Uri, onNotFound: (e: Exception) -> Unit) = requireContext().openBrowser(url, onNotFound)

fun Context.shareAsText(text: String, onAppNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivityCatching(shareIntent, onAppNotFound)
}

fun Fragment.shareAsText(text: String, onAppNotFound: (e: Exception) -> Unit) =
    requireContext().shareAsText(text, onAppNotFound)


fun Fragment.doOnBackPressed(action: OnBackPressedCallback) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, action)
}

fun Context.sendEmail(mail: Email, onNotFound: (e: Exception) -> Unit) {
    val sendIntent: Intent = Intent(Intent.ACTION_SEND).apply {
        //EXTRA_EMAIL should be array
        putExtra(Intent.EXTRA_EMAIL, mail.recipients?.toTypedArray())
        putExtra(Intent.EXTRA_SUBJECT, mail.subject)
        putExtra(Intent.EXTRA_TEXT, mail.body)
        //RFC standard for email
        type = "message/rfc822"
    }
    startActivityCatching(sendIntent, onNotFound)
}

fun Fragment.sendEmail(mail: Email, onNotFound: (e: Exception) -> Unit) =
    requireContext().sendEmail(mail, onNotFound)

fun Fragment.sendEmail(uri: Uri, onNotFound: (e: Exception) -> Unit) = sendEmail(Email.ofUri(uri), onNotFound)

data class Email(
    val recipients: List<String>? = null,
    val subject: String? = null,
    val body: String? = null,
) {
    companion object {
        fun ofUri(uri: Uri): Email {
            val mail = MailTo.parse(uri)
            return Email(
                mail.to?.split(", "),
                mail.subject,
                mail.body
            )
        }
    }
}

/**
 * Number uri of the form tel:+1234567890, containing countryCode
 */
fun Context.dialNumber(numberUri: Uri, onNotFound: (e: Exception) -> Unit) {
    val intent = Intent(Intent.ACTION_DIAL, numberUri)
    startActivityCatching(intent, onNotFound)
}

fun Fragment.dialNumber(numberUri: Uri, onNotFound: (e: Exception) -> Unit) =
    requireContext().dialNumber(numberUri, onNotFound)
