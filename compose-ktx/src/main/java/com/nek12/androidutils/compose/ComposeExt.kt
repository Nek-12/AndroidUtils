package com.nek12.androidutils.compose

import android.text.format.DateFormat
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource


val isSystem24Hour @Composable get() = DateFormat.is24HourFormat(LocalContext.current)

@Composable
fun Int.plural(
    quantity: Int,
    vararg formatArgs: Any? = emptyArray(),
): String {
    return LocalContext.current.resources.getQuantityString(this, quantity, *formatArgs)
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
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

val displayDensity: Int @Composable get() = LocalConfiguration.current.densityDpi

val screenWidthDp: Int @Composable get() = LocalConfiguration.current.screenWidthDp

val screenHeigthDp: Int @Composable get() = LocalConfiguration.current.screenHeightDp

val screenWidthPx: Int @Composable get() = screenWidthDp * displayDensity
