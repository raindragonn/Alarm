package com.bluepig.alarm.domain.result


fun <T> resultLoading(): Result<T> {
    return Result.failure(LoadingException)
}

fun <T> Result<T>.onFailureWitLoading(
    loadingAction: (() -> Unit)? = null,
    failureAction: (t: Throwable) -> Unit
): Result<T> {
    onFailure { throwable ->
        if (isLoading) {
            loadingAction?.invoke()
        } else {
            failureAction.invoke(throwable)
        }
    }
    return this
}

val <T> Result<T>.isLoading: Boolean
    get() = this.isFailure && exceptionOrNull() == LoadingException

