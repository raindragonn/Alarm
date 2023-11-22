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
    val repeatWeak: Set<Weak> = setOf(),
    val volume: Int? = null,
    val hasVibration: Boolean = true,
    val memo: String = "",
) : BaseEntity {
    fun getCalendar(): Calendar {
        return CalendarHelper.now.apply {
            timeInMillis = this@Alarm.timeInMillis
        }
    }
}
