package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentTime @Inject constructor() {
    suspend operator fun invoke() = flow {
        while (true) {
            val nowTime = CalendarHelper.now.timeInMillis
            emit(nowTime)
            delay(100L)
        }
    }
}