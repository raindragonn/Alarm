package com.bluepig.alarm.domain.result


fun <T> resultLoading(): Result<T> {
    return Result.failure(LoadingException)
}

fun <T> Result<T>.onFailureWithoutLoading(action: (exception: Throwable) -> Unit): Result<T> {
    onFailure {
        if (isLoading) return@onFailure
        action(it)
    }
    return this
}

fun <T> Result<T>.onLoading(loadingAction: () -> Unit): Result<T> {
    if (isLoading) {
        loadingAction()
    }
    return this
}

val <T> Result<T>.isLoading: Boolean
    get() = this.isFailure && exceptionOrNull() == LoadingException

