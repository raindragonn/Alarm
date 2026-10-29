package com.bluepig.alarm.domain.result

import kotlin.coroutines.cancellation.CancellationException

fun <T> resultLoading(): Result<T> {
    return Result.failure(LoadingException)
}

fun <T> resultInit(): Result<T> {
    return Result.failure(InitialException)
}

val <T> Result<T>.isLoading: Boolean
    get() = this.isFailure && exceptionOrNull() == LoadingException

val Throwable.isLoading
    get() = this is LoadingException

val Throwable.isInit
    get() = this is InitialException

/** [runCatching]과 같지만 코루틴 취소([CancellationException])는 삼키지 않고 다시 던진다 */
inline fun <T> runSuspendCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
