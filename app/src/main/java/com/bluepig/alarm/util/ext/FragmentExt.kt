package com.bluepig.alarm.util.ext

import androidx.fragment.app.Fragment

fun Fragment.showErrorToast(throwable: Throwable? = null, moreAction: (() -> Unit)? = null) {
    requireContext().showErrorToast(throwable, moreAction)
}
