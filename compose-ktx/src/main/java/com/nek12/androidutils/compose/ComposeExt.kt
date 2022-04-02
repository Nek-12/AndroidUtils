package com.nek12.androidutils.compose

import android.text.format.DateFormat
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


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


@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
): Flow<T> = remember(key1 = flow, key2 = lifecycleOwner) {
    flow.flowWithLifecycle(
        lifecycleOwner.lifecycle,
        Lifecycle.State.STARTED
    )
}

@Composable
fun <T : R, R> Flow<T>.collectAsStateOnLifecycle(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext,
): State<R> {
    val lifecycleAwareFlow = rememberFlowWithLifecycle(flow = this)
    return lifecycleAwareFlow.collectAsState(initial = initial, context = context)
}

@Suppress("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectAsStateOnLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsStateOnLifecycle(value, context)

@Composable
@Suppress("ComposableNaming")
inline fun <T> Flow<T>.collectOnLifecycle(
    crossinline action: suspend CoroutineScope.(T) -> Unit,
) {
    val lifecycleAwareFlow = rememberFlowWithLifecycle(this)
    LaunchedEffect(this) {
        lifecycleAwareFlow.collect { action(it) }
    }
}
