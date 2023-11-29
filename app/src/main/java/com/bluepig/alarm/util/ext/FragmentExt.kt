package com.bluepig.alarm.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val Fragment.viewLifeCycleScope: CoroutineScope
    get() = viewLifecycleOwner.lifecycleScope

fun Fragment.viewRepeatOnLifeCycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifeCycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state, block)
    }
}

fun Fragment.showErrorToast(throwable: Throwable? = null, moreAction: (() -> Unit)? = null) {
    requireContext().showErrorToast(throwable, moreAction)
}