package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.alarm.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarmFlow(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun getById(id: Long): Alarm?
    suspend fun deleteById(id: Long)
    suspend fun getAllAlarms(): List<Alarm>
}