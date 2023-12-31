package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class GetExpiredTime @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(calendar: Calendar, repeatWeeks: Set<Week>) =
        withContext(_dispatcher) {
            val now = CalendarHelper.now.timeInMillis
            val time = CalendarHelper.getNextTimeMillis(calendar, repeatWeeks)
            time - now
        }
}