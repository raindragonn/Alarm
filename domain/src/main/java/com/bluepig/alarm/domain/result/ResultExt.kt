package com.bluepig.alarm.domain.result


fun <T> resultLoading(): Result<T> {
    return Result.failure(LoadingException)
}

val <T> Result<T>.isLoading: Boolean
    get() = this.isFailure && exceptionOrNull() == LoadingException

val Throwable.isLoading
    get() = this is LoadingException
