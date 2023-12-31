package com.bluepig.alarm.util.ext

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Week


val Week.stringId
    @StringRes get() = when (this) {
        Week.SUNDAY -> R.string.week_sun
        Week.MONDAY -> R.string.week_mon
        Week.TUESDAY -> R.string.week_tues
        Week.WEDNESDAY -> R.string.week_wed
        Week.THURSDAY -> R.string.week_thu
        Week.FRIDAY -> R.string.week_fri
        Week.SATURDAY -> R.string.week_sat
    }

fun Set<Week>.getGuideText(context: Context): String {
    val weekEntries = Week.entries
    return when {
        this.isEmpty() -> context.getString(R.string.week_not_select_guide)

        this == weekEntries.filter { it.isWeekend }
            .toSet() -> context.getString(R.string.week_weekend_guide)

        this == weekEntries.filter { !it.isWeekend }
            .toSet() -> context.getString(R.string.week_weekday_guide)

        this == weekEntries.toSet() -> context.getString(R.string.week_everyday_guide)

        else -> sorted().joinToString(" ") { context.getString(it.stringId) } + context.getString(R.string.week_guide)
    }
}

@ColorInt
fun Set<Week>.tintColor(context: Context, isActive: Boolean): Int {
    if (!isActive) {
        return context.getColor(R.color.disable)
    }
    return if (this.all { it.isWeekend } && this.isNotEmpty()) {
        context.getColor(R.color.misc_050)
    } else {
        context.getColor(R.color.primary_600)
    }
}