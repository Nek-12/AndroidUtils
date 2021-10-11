@file:Suppress("unused")

package com.nek12.androidutils.extensions.coroutines

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> Collection<T>.forEachParallel(f: suspend (T) -> Unit): Unit = runBlocking {
    map { async { f(it) } }.forEach { it.await() }
}

fun <A, B> List<A>.mapParallel(f: suspend (A) -> B): List<B> = runBlocking {
    map { async { f(it) } }.map { it.await() }
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.postValue(this.value)
}

/** starts a new coroutine that will collect values from a flow while the lifecycle is in a [state] **/
fun <T> Flow<T>.collectOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    action: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            collect { action(it) }
        }
    }
}

fun <T> Flow<T?>.toState(scope: CoroutineScope): StateFlow<T?> {
    return stateIn(scope, SharingStarted.WhileSubscribed(), null)
}

fun <T> Flow<T>.toState(scope: CoroutineScope, initialValue: T): StateFlow<T> {
    return stateIn(scope, SharingStarted.WhileSubscribed(), initialValue)
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit,
) = viewModelScope.launch(context, block = block)


fun LifecycleOwner.delayOnLifecycle(
    delayMs: Long,
    action: suspend () -> Unit,
) {
    lifecycleScope.launch {
        delay(delayMs)
        action()
    }
}
