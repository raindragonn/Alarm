package com.bluepig.repository

import com.bluepig.alarm.database.dao.AlarmDao
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.mapper.AlarmMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _dao: AlarmDao,
) : AlarmRepository {

    override suspend fun getAllAlarms(): Flow<List<Alarm>> =
        withContext(_dispatcher) {
            _dao.getAll()
                .map { list ->
                    list.map(AlarmMapper::mapToEntity)
                }
        }

    override suspend fun insertAlarm(alarm: Alarm): Long =
        withContext(_dispatcher) {
            val data = AlarmMapper.mapToData(alarm)
            _dao.insert(data)
        }

    override suspend fun updateAlarm(alarm: Alarm): Unit =
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

    override suspend fun deleteById(id: Long): Unit =
        withContext(_dispatcher) {
            _dao.deleteById(id)
        }
}