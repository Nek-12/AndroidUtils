@file:Suppress("unused")

package com.nek12.androidutils.compose

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.text.format.DateFormat
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.nek12.androidutils.extensions.android.Text
import com.nek12.androidutils.extensions.android.Text.Dynamic
import com.nek12.androidutils.extensions.android.Text.Resource

val isSystem24Hour @Composable get() = DateFormat.is24HourFormat(LocalContext.current)

@ExperimentalComposeUiApi
@Composable
fun Int.plural(
    quantity: Int,
    vararg formatArgs: Any = emptyArray(),
): String {
    return pluralStringResource(this, quantity, *formatArgs)
}

@ExperimentalAnimationGraphicsApi
@Composable
fun Int.animatedVector() = AnimatedImageVector.animatedVectorResource(id = this)

@Composable
fun Int?.string() = this?.let { stringResource(id = this) }

@Composable
fun Int.string() = stringResource(id = this)

@Composable
fun Int?.string(vararg args: Any) = this?.let { stringResource(id = this, *args) }

@Composable
fun Int.string(vararg args: Any) = stringResource(id = this, *args)

/**
 * Produces an annotated string identical to the original string
 * For those times when an api requires AnnotatedString but you don't want to build one
 */
@Composable
fun String.annotate() = AnnotatedString(this)

@Composable
@Suppress("ComposableEventParameterNaming")
fun String.annotate(builder: AnnotatedString.Builder.(String) -> Unit) = buildAnnotatedString { builder(this@annotate) }

val displayDensity: Int @Composable get() = LocalConfiguration.current.densityDpi

val screenWidthDp: Int @Composable get() = LocalConfiguration.current.screenWidthDp

val screenHeigthDp: Int @Composable get() = LocalConfiguration.current.screenHeightDp

val screenWidthPx: Int @Composable get() = screenWidthDp * displayDensity

@Composable
inline fun <reified BoundService : Service, reified BoundServiceBinder : Binder> rememberBoundLocalService(
    flags: Int = Context.BIND_AUTO_CREATE,
    crossinline getService: @DisallowComposableCalls BoundServiceBinder.() -> BoundService,
): State<BoundService?> {
    val context: Context = LocalContext.current
    val boundService = remember(context) { mutableStateOf<BoundService?>(null) }
    val serviceConnection: ServiceConnection = remember(context) {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                boundService.value = (service as BoundServiceBinder).getService()
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                boundService.value = null
            }
        }
    }
    DisposableEffect(context, serviceConnection) {
        context.bindService(Intent(context, BoundService::class.java), serviceConnection, flags)

        onDispose { context.unbindService(serviceConnection) }
    }
    return boundService
}

@Composable
fun Text.string(): String = when (this) {
    is Dynamic -> text
    is Resource -> string(LocalContext.current)
}
