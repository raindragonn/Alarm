package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.alarm.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarmFlow(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun getById(id: Long): Alarm?
    suspend fun deleteById(id: Long)
    suspend fun getAllAlarms(): List<Alarm>

    /** 마지막으로 예약할 때의 시간대 ID. 바뀌면 같은 벽시계 시각으로 다시 계산한다 (U1) */
    suspend fun getScheduledTimeZoneId(): String?
    suspend fun setScheduledTimeZoneId(id: String)
}