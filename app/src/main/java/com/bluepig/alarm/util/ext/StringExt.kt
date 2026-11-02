package com.bluepig.alarm.util.ext

import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableStringBuilder

fun String.createSpan(
    startIndex: Int,
    endIndex: Int,
    vararg spans: ParcelableSpan
): SpannableStringBuilder {
    return SpannableStringBuilder(this)
        .apply {
            for (span in spans) {
                setSpan(
                    span,
                    startIndex,
                    endIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
}