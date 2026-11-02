package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.util.CalendarHelper
import java.util.Calendar
import javax.inject.Inject

class GetExpiredTime @Inject constructor() {
    operator fun invoke(calendar: Calendar, repeatWeeks: Set<Week>): Long {
        val now = CalendarHelper.now.timeInMillis
        val time = CalendarHelper.getNextTimeMillis(calendar, repeatWeeks)
        return time - now
    }
}
