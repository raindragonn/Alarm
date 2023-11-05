package com.bluepig.repository

import com.bluepig.alarm.database.dao.AlarmDao
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.mapper.AlarmMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _dao: AlarmDao,
) : AlarmRepository {

    override suspend fun getAllAlarms(): List<Alarm> =
        withContext(_dispatcher) {
            _dao.getAll()
                .map(AlarmMapper::mapToEntity)
        }

    override suspend fun insertAlarm(alarm: Alarm): Long =
        withContext(_dispatcher) {
            val data = AlarmMapper.mapToData(alarm)
            _dao.insert(data)
        }

    override suspend fun updateAlarm(alarm: Alarm) =
        withContext(_dispatcher) {
            val data = AlarmMapper.mapToData(alarm)
            _dao.update(data)
        }

    override suspend fun getById(id: Long): Alarm? =
        withContext(_dispatcher) {
            _dao.getById(id)?.let { data ->
                AlarmMapper.mapToEntity(data)
            }
        }

    override suspend fun deleteById(id: Long) =
        withContext(_dispatcher) {
            _dao.deleteById(id)
        }
}