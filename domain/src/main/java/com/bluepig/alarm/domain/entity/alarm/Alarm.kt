package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.base.BaseEntity
import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long = CalendarHelper.now.timeInMillis,
    val isActive: Boolean = true,
    val media: AlarmMedia,
    val repeatWeek: Set<Week> = setOf(),
    val volume: Int,
    val isVolumeAutoIncrease: Boolean = true,
    val hasVibration: Boolean = true,
    val memo: String = "",
    val memoTtsEnabled: Boolean = false,
) : BaseEntity, java.io.Serializable {
    fun getCalendar(): Calendar {
        return CalendarHelper.now.apply {
            timeInMillis = this@Alarm.timeInMillis
        }
    }

    fun getActiveCheckedAlarm(): Alarm {
        if (!isActive) return this
        val now = CalendarHelper.now
        val hasExpired = now.after(CalendarHelper.fromTimeInMillis(timeInMillis))
        val hasActive = repeatWeek.isNotEmpty() || !hasExpired

        return if (hasActive) {
            getNextTimeAlarm()
        } else {
            copy(isActive = false)
        }
    }

    /**
     * Get next time alarm
     * [repeatWeek]에 따라 [timeInMillis]를 새로 계산해서 반환한다.
     * @return
     */
    fun getNextTimeAlarm(): Alarm {
        if (isActive.not()) return this

        return copy(
            timeInMillis = CalendarHelper.getNextTimeMillis(
                getCalendar(),
                repeatWeek
            )
        )
    }
}