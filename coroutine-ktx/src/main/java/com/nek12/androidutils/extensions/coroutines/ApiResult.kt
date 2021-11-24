@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.nek12.androidutils.extensions.coroutines

import kotlinx.coroutines.flow.Flow

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

    /**
     * Returns [Success.result] or null in all other cases.
     */
    fun getOrNull(): T? {
        return if (this is Success<T>) result else null
    }

    /**
     * Throws [Error.e], or [UnsupportedOperationException] if the request has not been completed yet.
     */
    fun getOrThrow(): T {
        return when (this) {
            is Loading -> throw UnsupportedOperationException("Request has not been completed yet")
            is Error -> throw e
            is Success -> result
        }
    }

    /**
     * Makes this result an error if [predicate] returns non-null exception
     */
    fun errorIf(predicate: (T) -> Exception?): ApiResult<T> {
        return when (this) {
            is Success -> {
                val error = predicate(result)
                if (error != null) {
                    Error(error)
                } else {
                    this
                }
            }
            else -> this
        }
    }

    /**
     * Makes this result an error if [predicate] returns false
     */
    fun errorIfNot(message: String = "Condition not satisfied", predicate: (T) -> Boolean): ApiResult<T> {
        return when {
            this is Success && !predicate(result) -> {
                Error(IllegalArgumentException(message))
            }
            else -> this
        }
    }

    /**
     * Change the type of the result to [R] without affecting error/loading results
     */
    inline fun <R> map(block: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> Success(block(result))
            is Error -> Error(e)
            is Loading -> this
        }
    }

    companion object {

        suspend inline fun <T> wrap(crossinline call: suspend () -> T): ApiResult<T> {
            return try {
                Success(call())
            } catch (e: Exception) {
                Error(e)
            }
        }

        inline fun <T> flow(crossinline call: suspend () -> T): Flow<ApiResult<T>> {
            return kotlinx.coroutines.flow.flow {
                emit(Loading)
                emit(wrap(call))
            }
        }
    }
}
