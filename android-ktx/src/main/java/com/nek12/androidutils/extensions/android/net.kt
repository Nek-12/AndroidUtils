package com.nek12.androidutils.extensions.android

import android.net.Uri

val Uri.asHttps: Uri get() = if (scheme == "http") buildUpon().scheme("https").build() else this
val String?.asUri get() = this?.let { Uri.parse(it) }
val Uri.linkType
    get() = when (this.scheme) {
        "http", "https" -> LinkType.Web
        "mailto" -> LinkType.Mail
        null -> LinkType.Unknown
        else -> LinkType.Other
    }

enum class LinkType {
    Web, Mail, Other, Unknown
}
