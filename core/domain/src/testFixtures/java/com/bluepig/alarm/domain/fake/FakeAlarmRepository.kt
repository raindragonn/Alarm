package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Room처럼 id가 null이면 새 id를 붙이고, 같은 id면 덮어쓴다.
class FakeAlarmRepository(vararg alarms: Alarm) : AlarmRepository {
    private val _alarms = MutableStateFlow(alarms.associateBy { requireNotNull(it.id) })

    override fun getAllAlarmFlow(): Flow<List<Alarm>> = _alarms.map { it.values.toList() }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        val id = alarm.id ?: ((_alarms.value.keys.maxOrNull() ?: 0L) + 1)
        _alarms.update { it + (id to alarm.copy(id = id)) }
        return id
    }

    override suspend fun getById(id: Long): Alarm? = _alarms.value[id]

    override suspend fun deleteById(id: Long) {
        _alarms.update { it - id }
    }

    override suspend fun getAllAlarms(): List<Alarm> = _alarms.value.values.toList()

    var scheduledTimeZoneId: String? = null
    override suspend fun getScheduledTimeZoneId() = scheduledTimeZoneId
    override suspend fun setScheduledTimeZoneId(id: String) {
        scheduledTimeZoneId = id
    }
}
