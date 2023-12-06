package com.bluepig.alarm.util.ext

import androidx.annotation.StringRes
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Week


val Week.stringId
    @StringRes
    get() = when (this) {
        Week.SUNDAY -> R.string.week_sun
        Week.MONDAY -> R.string.week_mon
        Week.TUESDAY -> R.string.week_tues
        Week.WEDNESDAY -> R.string.week_wed
        Week.THURSDAY -> R.string.week_thu
        Week.FRIDAY -> R.string.week_fri
        Week.SATURDAY -> R.string.week_sat
    }