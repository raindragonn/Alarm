package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.base.BaseEntity
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long = CalendarHelper.now.timeInMillis,
    val isActive: Boolean = true,
    val file: File,
    val repeatWeek: Set<Week> = setOf(),
    val volume: Int? = null,
    val hasVibration: Boolean = true,
    val memo: String = "",
) : BaseEntity, java.io.Serializable {
    fun getCalendar(): Calendar {
        return CalendarHelper.now.apply {
            timeInMillis = this@Alarm.timeInMillis
        }
    }
    // TODO: Week 포함하는 경우 timeInMillis를 변경하는 것이 아닌 hour, minute을 통해 NextAlarmTimeInMillis를 구하는걸로 고려하기
}
