package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.base.BaseEntity
import com.bluepig.alarm.domain.util.CalendarHelper
import java.util.Calendar

data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long,
    val isActive: Boolean,
    val songName: String,
) : BaseEntity {
    fun getCalendar(): Calendar {
        return CalendarHelper.now.apply {
            timeInMillis = this@Alarm.timeInMillis
        }
    }
}
