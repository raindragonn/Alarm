@file:Suppress("unused")

package com.bluepig.alarm.domain.result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

sealed interface BpResult<out T> {
    data class Success<T>(val data: T) : BpResult<T>
    data class Failure(val exception: Throwable? = null) : BpResult<Nothing>
    data object Loading : BpResult<Nothing>
}

val <T> BpResult<T>.exception: Throwable?
    get() = if (this is BpResult.Failure) this.exception else null

suspend fun <T> asyncResultWithContextOf(
    dispatcher: CoroutineDispatcher,
    transform: suspend () -> T
): BpResult<T> {
    val kotlinResult = runCatching {
        withContext(dispatcher) {
            transform.invoke()
        }
    }
    return if (kotlinResult.isSuccess) {
        BpResult.Success(kotlinResult.getOrThrow())
    } else {
        BpResult.Failure(kotlinResult.exceptionOrNull())
    }
}

fun <T> resultOf(transform: () -> T): BpResult<T> {
    val kotlinResult = runCatching {
        transform.invoke()
    }
    return if (kotlinResult.isSuccess) {
        BpResult.Success(kotlinResult.getOrThrow())
    } else {
        BpResult.Failure(kotlinResult.exceptionOrNull())
    }
}


fun <T> BpResult<T>.isSuccess(): Boolean {
    return this is BpResult.Success
}

fun <T> BpResult<T>.isFailure(): Boolean {
    return this is BpResult.Failure
}

fun <T> BpResult<T>.isLoading(): Boolean {
    return this is BpResult.Failure
}

fun <T> BpResult<T>.getOrThrow(): T {
    return when (this) {
        is BpResult.Failure -> throw exception ?: FailureException
        is BpResult.Loading -> throw LoadingException
        is BpResult.Success -> data
    }
}

fun <T> BpResult<T>.getOrNull(): T? {
    return when (this) {
        is BpResult.Loading -> null
        is BpResult.Failure -> null
        is BpResult.Success -> data
    }
}

fun <T> BpResult<T>.onLoading(executable: () -> Unit): BpResult<T> = apply {
    if (this is BpResult.Loading) {
        executable.invoke()
    }
}

fun <T> BpResult<T>.onSuccess(executable: (T) -> Unit): BpResult<T> = apply {
    if (this is BpResult.Success) {
        executable.invoke(data)
    }
}

fun <T> BpResult<T>.onFailure(executable: (e: Throwable?) -> Unit): BpResult<T> = apply {
    if (this is BpResult.Failure) {
        executable.invoke(exception)
    }
}

suspend fun <T> BpResult<T>.asyncSuccess(executable: suspend (T) -> Unit): BpResult<T> = apply {
    if (this is BpResult.Success) {
        executable.invoke(data)
    }
}

suspend fun <T> BpResult<T>.asyncFailure(executable: suspend (e: Throwable?) -> Unit): BpResult<T> =
    apply {
        if (this is BpResult.Failure) {
            executable.invoke(exception)
        }
    }