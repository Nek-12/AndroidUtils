@file:Suppress("MemberVisibilityCanBePrivate", "unused", "NOTHING_TO_INLINE")

package com.nek12.androidutils.extensions.core

import com.nek12.androidutils.extensions.core.ApiResult.*

class NotFinishedException : IllegalArgumentException(ApiResult.DEFAULT_LOADING_MESSAGE)

/**
 * A class that wraps a result of a network call.
 */
sealed class ApiResult<out T> {

    /**
     * Use this to indicate result loading state
     */
    object Loading : ApiResult<Nothing>()

    /**
     * Request has been performed successfully
     */
    data class Success<out T>(val result: T) : ApiResult<T>()

    /**
     * There was an error completing the request.
     */
    data class Error(val e: Exception) : ApiResult<Nothing>() {

        val message get() = e.message
    }

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    companion object {

        @PublishedApi
        internal const val DEFAULT_ERROR_MESSAGE = "Condition not satisfied"

        @PublishedApi
        internal const val DEFAULT_LOADING_MESSAGE = "ApiResult is still in Loading state"
    }
}

inline fun <R, T : R> ApiResult<T>.or(defaultValue: R): R = orElse { defaultValue }

inline fun <T> ApiResult<List<T>>.orEmpty(): List<T> = or(emptyList())

inline fun <T> ApiResult<Set<T>>.orEmpty(): Set<T> = or(emptySet())

inline fun <T> ApiResult<Collection<T>>.orEmpty(): Collection<T> = or(emptyList())

inline fun <T> ApiResult<T>.orNull(): T? = or(null)

/**
 * Throws [ApiResult.Error.e], or [NotFinishedException] if the request has not been completed yet.
 */
inline fun <T> ApiResult<T>.orThrow(): T {
    return when (this) {
        is Loading -> throw NotFinishedException()
        is Error -> throw e
        is Success -> result
    }
}

/**
 * [Loading] will result in [NotFinishedException]
 */
inline fun <R, T : R> ApiResult<T>.orElse(action: (e: Exception) -> R): R = when (this) {
    is Success -> result
    is Error -> action(e)
    is Loading -> action(NotFinishedException())
}

/**
 * By default, maps [Loading] to Error witn [NotFinishedException]
 */
inline fun <T, R> ApiResult<T>.fold(
    onSuccess: (result: T) -> R,
    onError: (exception: Exception) -> R,
    noinline onLoading: (() -> R)? = null,
): R {
    return when (this) {
        is Success -> onSuccess(result)
        is Error -> onError(e)
        is Loading -> onLoading?.let { it() } ?: onError(NotFinishedException())
    }
}

inline fun <T> ApiResult<T>.onError(block: (Exception) -> Unit): ApiResult<T> {
    if (this is Error) block(e)
    return this
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is Success) block(result)
    return this
}

inline fun <T> ApiResult<T>.onLoading(block: () -> Unit): ApiResult<T> {
    if (this is Loading) block()
    return this
}

/**
 * Makes the result an error if [predicate] returns false
 */
inline fun <T> ApiResult<T>.errorIfNot(
    exception: Exception,
    crossinline predicate: (T) -> Boolean,
): ApiResult<T> = errorIf(exception) { !predicate(it) }

/**
 * Makes this result an error if [predicate] returns true
 */
inline fun <T> ApiResult<T>.errorIf(
    exception: Exception,
    crossinline predicate: (T) -> Boolean,
): ApiResult<T> = if (this is Success && predicate(result)) Error(exception) else this

/**
 * Change the type of the result to [R] without affecting error/loading results
 */
inline fun <T, R> ApiResult<T>.map(crossinline block: (T) -> R): ApiResult<R> {
    return when (this) {
        is Success -> Success(block(result))
        is Error -> Error(e)
        is Loading -> this
    }
}

/**
 * Change the exception of the Error response without affecting loading/success results
 */
inline fun <T, R : Exception> ApiResult<T>.mapError(crossinline block: (Exception) -> R): ApiResult<T> {
    return when (this) {
        is Success -> this
        is Error -> Error(block(e))
        is Loading -> this
    }
}

/**
 * Maps [Loading] to a [Success], not touching anything else
 */
inline fun <R, T : R> ApiResult<T>.mapLoading(crossinline block: () -> R): ApiResult<R> {
    return when (this) {
        is Success -> this
        is Error -> this
        is Loading -> Success(block())
    }
}
