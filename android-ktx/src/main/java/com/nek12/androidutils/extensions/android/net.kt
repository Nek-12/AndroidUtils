package com.nek12.androidutils.extensions.android

import android.net.Uri

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
