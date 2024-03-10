@file:Suppress("unused")

package com.nek12.androidutils.extensions.coroutines

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Convert this flow to a stateflow that will be observed on this scope and sharing is going to
 * continue while there are active subscribers. Most similar to livedata
 */
fun <T> Flow<T?>.toState(scope: CoroutineScope): StateFlow<T?> = stateIn(scope, SharingStarted.WhileSubscribed(), null)

/**
 * Convert this flow to a stateflow that will be observed on this scope and sharing is going to
 * continue while there are active subscribers. Instead of null like in live data, has an
 * [initialValue]
 */
fun <T> Flow<T>.toState(scope: CoroutineScope, initialValue: T): StateFlow<T> =
    stateIn(scope, SharingStarted.WhileSubscribed(), initialValue)

/**
 * Launches the specified block as a coroutine
 */
fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(context, start, block)

/**
 * A lifecycle- and thread- safe implementation of [android.os.Handler.postDelayed] that was
 * dangerous, buggy and outdated. The action is cancelled if the lifecycle is in a destroyed state. Use
 * [Fragment.getViewLifecycleOwner] for best safety with this method.
 */
fun LifecycleOwner.delayOnLifecycle(
    delayMs: Long,
    action: suspend () -> Unit,
) {
    lifecycleScope.launch {
        delay(delayMs)
        action()
    }
}

fun View.clicks(delay: Long = 1000L) = callbackFlow {
    var lastTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime >= delay) {
            lastTime = currentTime
            trySend(it)
        }
    }
    awaitClose {
        setOnClickListener(null)
    }
}
