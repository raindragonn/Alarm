package com.bluepig.alarm.data.fake

import com.bluepig.alarm.database.AlarmData
import com.bluepig.alarm.database.AlarmLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Room처럼 id가 null이면 새 id를 붙이고, 같은 id면 덮어쓴다.
class FakeAlarmLocalDataSource : AlarmLocalDataSource {
    private val _alarms = MutableStateFlow(mapOf<Long, AlarmData>())

    override fun getAllFlow(): Flow<List<AlarmData>> = _alarms.map { it.values.toList() }
    override suspend fun getAll() = _alarms.value.values.toList()
    override suspend fun getById(id: Long) = _alarms.value[id]

    override suspend fun insert(alarm: AlarmData): Long {
        val id = alarm.id ?: ((_alarms.value.keys.maxOrNull() ?: 0L) + 1)
        _alarms.update { it + (id to alarm.copy(id = id)) }
        return id
    }

    override suspend fun deleteById(id: Long) {
        _alarms.update { it - id }
    }
}
