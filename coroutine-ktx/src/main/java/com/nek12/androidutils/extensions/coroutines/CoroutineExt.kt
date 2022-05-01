@file:Suppress("unused")

package com.nek12.androidutils.extensions.coroutines

import android.view.View
import androidx.lifecycle.*
import com.nek12.androidutils.extensions.core.ApiResult
import com.nek12.androidutils.extensions.core.map
import com.nek12.androidutils.extensions.core.wrap
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Execute [block] in parallel using operator async for each element of the collection
 */
suspend fun <T> Collection<T>.forEachParallel(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.(T) -> Unit,
): Unit = withContext(context) {
    map { async(context, start) { this@withContext.block(it) } }.forEach { it.await() }
}

/**
 * Execute [block] in parallel using operator async for each element of the collection
 */
suspend fun <A, B> Collection<A>.mapParallel(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.(A) -> B,
): List<B> = withContext(context) {
    map { async(context, start) { this@withContext.block(it) } }.map { it.await() }
}

/** Starts a new coroutine that will collect values from a flow while the lifecycle is in a
 * [state].
 *
 * This function is most useful in Fragments or other components with a [lifecycleOwner]
 * Your [Flow] will be collected as long as the [lifecycleOwner] is in the specified state,
 * automatically stopping when it leaves the scope.
 *
 * example for a [Fragment]:
 * ```
 *  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *      viewModel.myDataFlow.collectOnLifecycle(viewLifecycleOwner) {
 *          //safely process your data using suspend lambda
 *      }
 *  }
 * ```
 *
 * **/
fun <T> Flow<T>.collectOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend CoroutineScope.(T) -> Unit,
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, state).collect { collector(it) }
}

fun <T> Flow<T?>.collectNotNullOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    collector: suspend CoroutineScope.(T) -> Unit,
) {
    collectOnLifecycle(lifecycleOwner, state) {
        if (it != null) collector(it)
    }
}

/**
 * Convert this flow to a stateflow that will be observed on this scope and sharing is going to
 * continue while there are active subscribers. Most similar to livedata
 */
fun <T> Flow<T?>.toState(scope: CoroutineScope): StateFlow<T?> {
    return stateIn(scope, SharingStarted.WhileSubscribed(), null)
}

/**
 * Convert this flow to a stateflow that will be observed on this scope and sharing is going to
 * continue while there are active subscribers. Instead of null like in live data, has an
 * [initialValue]
 */
fun <T> Flow<T>.toState(scope: CoroutineScope, initialValue: T): StateFlow<T> {
    return stateIn(scope, SharingStarted.WhileSubscribed(), initialValue)
}

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

/**
 * Emits [Loading], then executes [call] and [wrap]s it in [ApiResult]
 */
inline fun <T> ApiResult.Companion.flow(crossinline call: suspend () -> T): Flow<ApiResult<T>> {
    return kotlinx.coroutines.flow.flow {
        emit(ApiResult.Loading)
        emit(wrap { call() })
    }
}

inline fun <T, R> Flow<ApiResult<T>>.map(crossinline transform: suspend (T) -> R): Flow<ApiResult<R>> =
    map { result -> result.map { transform(it) } }


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

fun CoroutineScope.launchCatching(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onError: CoroutineContext.(Throwable) -> Unit,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val handler = CoroutineExceptionHandler { context, e ->
        onError(context, e)
    }
    return launch(dispatcher + handler, start, block)
}
